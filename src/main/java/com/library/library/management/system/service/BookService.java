package com.library.library.management.system.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.library.library.management.system.entity.Book;
import com.library.library.management.system.repository.BookRepository;

@Service
public class BookService {

    private final BookRepository bookRepository;

    // Constructor Injection
    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    // Save Book
    public Book saveBook(Book book) {
        return bookRepository.save(book);
    }

    // Get All Books
    public List<Book> getAllBooks() {
        return bookRepository.findByDeletedFalse();
    }

    // Get Books with Pagination
    public Page<Book> getBooksPage(int page, int size) {

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by("id").descending()
        );

        return bookRepository.findByDeletedFalse(pageable);
    }

    // Get Book By ID
    public Book getBookById(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "Book not found with ID: " + id));
    }

    // Delete Book
    public void deleteBook(Long id) {
        Book book = getBookById(id);
        book.setDeleted(true);
        bookRepository.save(book);
    }

    // Count Total Books
    public long countBooks() {
        return bookRepository.countByDeletedFalse();
    }

    // Update Book
    public Book updateBook(Long id, Book updatedBook) {

        Book existingBook = getBookById(id);

        int issuedCopies = existingBook.getQuantity()
                - existingBook.getAvailableCopies();

        existingBook.setTitle(updatedBook.getTitle());
        existingBook.setAuthor(updatedBook.getAuthor());
        existingBook.setIsbn(updatedBook.getIsbn());
        existingBook.setCategory(updatedBook.getCategory());
        existingBook.setQuantity(updatedBook.getQuantity());

        existingBook.setAvailableCopies(
                updatedBook.getQuantity() - issuedCopies
        );

        return bookRepository.save(existingBook);
    }

    // Search Books by Title, Author or ISBN
    public List<Book> searchBooks(String keyword) {

        if (keyword == null || keyword.trim().isEmpty()) {
            return bookRepository.findByDeletedFalse();
        }

        String searchKeyword = keyword.trim();

        return bookRepository.searchActiveBooks(searchKeyword, Pageable.unpaged()).getContent();
    }

    // Search Books with Pagination
    public Page<Book> searchBooksPage(String keyword, int page, int size) {

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by("id").descending()
        );

        if (keyword == null || keyword.trim().isEmpty()) {
            return bookRepository.findByDeletedFalse(pageable);
        }

        String searchKeyword = keyword.trim();

        return bookRepository.searchActiveBooks(searchKeyword, pageable);
    }

    // Check duplicate ISBN while adding
    public boolean isIsbnExists(String isbn) {
        return bookRepository.existsByIsbn(isbn);
    }

    // Check duplicate ISBN while editing
    public boolean isIsbnExistsForOtherBook(String isbn, Long id) {
        return bookRepository.existsByIsbnAndIdNot(isbn, id);
    }
}
