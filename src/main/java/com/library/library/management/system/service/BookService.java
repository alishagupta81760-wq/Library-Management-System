package com.library.library.management.system.service;

import java.util.List;

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
        return bookRepository.findAll();
    }

    // Get Book By ID
    public Book getBookById(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Book not found with ID: " + id));
    }

    // Delete Book
    public void deleteBook(Long id) {
        bookRepository.deleteById(id);
    }

    // Count Total Books
    public long countBooks() {
        return bookRepository.count();
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
 // Search Books
    public List<Book> searchBooks(String keyword) {

        if (keyword == null || keyword.trim().isEmpty()) {
            return bookRepository.findAll();
        }

        return bookRepository.findByTitleContainingIgnoreCase(keyword);
    }
}