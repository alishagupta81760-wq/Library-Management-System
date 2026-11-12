package com.library.library.management.system.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import com.library.library.management.system.entity.Book;
import com.library.library.management.system.repository.BookRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookService bookService;

    @Test
    void saveBookReturnsRepositoryResult() {
        Book book = new Book();
        when(bookRepository.save(book)).thenReturn(book);

        assertSame(book, bookService.saveBook(book));
        verify(bookRepository).save(book);
    }

    @Test
    void updateBookPreservesIssuedCopiesWhenQuantityChanges() {
        Book existing = new Book();
        existing.setQuantity(8);
        existing.setAvailableCopies(5);
        Book changes = new Book();
        changes.setTitle("Updated title");
        changes.setAuthor("Updated author");
        changes.setIsbn("new-isbn");
        changes.setCategory("Science");
        changes.setQuantity(10);
        when(bookRepository.findById(4L)).thenReturn(Optional.of(existing));
        when(bookRepository.save(existing)).thenReturn(existing);

        Book result = bookService.updateBook(4L, changes);

        assertSame(existing, result);
        assertEquals("Updated title", existing.getTitle());
        assertEquals(10, existing.getQuantity());
        assertEquals(7, existing.getAvailableCopies());
        verify(bookRepository).save(existing);
    }

    @Test
    void updateBookFailsWhenBookDoesNotExist() {
        when(bookRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException error = assertThrows(RuntimeException.class,
                () -> bookService.updateBook(99L, new Book()));

        assertEquals("Book not found with ID: 99", error.getMessage());
    }

    @Test
    void searchBooksPageTrimsKeywordAndUsesRequestedPage() {
        when(bookRepository
                .findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCaseOrIsbnContainingIgnoreCase(
                        any(), any(), any(), any()))
                .thenReturn(new PageImpl<>(java.util.List.of()));

        bookService.searchBooksPage("  Tolkien  ", 2, 5);

        verify(bookRepository)
                .findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCaseOrIsbnContainingIgnoreCase(
                        "Tolkien", "Tolkien", "Tolkien", PageRequest.of(2, 5,
                                org.springframework.data.domain.Sort.by("id").descending()));
    }
}
