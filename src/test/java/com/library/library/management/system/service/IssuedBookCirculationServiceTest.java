package com.library.library.management.system.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Optional;

import com.library.library.management.system.entity.Book;
import com.library.library.management.system.entity.IssuedBook;
import com.library.library.management.system.entity.Student;
import com.library.library.management.system.repository.BookRepository;
import com.library.library.management.system.repository.IssuedBookRepository;
import com.library.library.management.system.repository.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class IssuedBookCirculationServiceTest {

    @Mock
    private IssuedBookRepository issuedBookRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private StudentRepository studentRepository;

    @InjectMocks
    private IssuedBookService issuedBookService;

    private Student student;
    private Book book;

    @BeforeEach
    void setUp() {
        student = new Student();
        student.setId(2L);
        book = new Book();
        book.setId(7L);
        book.setQuantity(3);
        book.setAvailableCopies(3);
    }

    @Test
    void issueBookCreatesIssuedRecordAndDecreasesAvailableCopies() {
        when(studentRepository.findById(2L)).thenReturn(Optional.of(student));
        when(bookRepository.findById(7L)).thenReturn(Optional.of(book));
        when(issuedBookRepository.save(org.mockito.ArgumentMatchers.any(IssuedBook.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        LocalDate dueDate = LocalDate.now().plusDays(14);

        IssuedBook result = issuedBookService.issueBook(2L, 7L, dueDate);

        assertEquals(student, result.getStudent());
        assertEquals(book, result.getBook());
        assertEquals(LocalDate.now(), result.getIssueDate());
        assertEquals(dueDate, result.getDueDate());
        assertEquals("ISSUED", result.getStatus());
        assertEquals(2, book.getAvailableCopies());
        verify(bookRepository).save(book);
        verify(issuedBookRepository).save(result);
    }

    @Test
    void issueBookRejectsWhenNoCopiesAreAvailable() {
        book.setAvailableCopies(0);
        when(studentRepository.findById(2L)).thenReturn(Optional.of(student));
        when(bookRepository.findById(7L)).thenReturn(Optional.of(book));

        RuntimeException error = assertThrows(RuntimeException.class,
                () -> issuedBookService.issueBook(2L, 7L, LocalDate.now().plusDays(1)));

        assertEquals("Book is not available", error.getMessage());
    }

    @Test
    void issueBookRejectsPastDueDate() {
        when(studentRepository.findById(2L)).thenReturn(Optional.of(student));
        when(bookRepository.findById(7L)).thenReturn(Optional.of(book));

        RuntimeException error = assertThrows(RuntimeException.class,
                () -> issuedBookService.issueBook(2L, 7L, LocalDate.now().minusDays(1)));

        assertEquals("Due date must be today or a future date", error.getMessage());
        assertEquals(3, book.getAvailableCopies());
    }

    @Test
    void returnBookMarksRecordReturnedAndRestoresCopy() {
        IssuedBook issue = new IssuedBook();
        issue.setBook(book);
        issue.setStatus("ISSUED");
        book.setAvailableCopies(2);
        when(issuedBookRepository.findById(5L)).thenReturn(Optional.of(issue));
        when(issuedBookRepository.save(issue)).thenReturn(issue);

        IssuedBook result = issuedBookService.returnBook(5L);

        assertEquals("RETURNED", result.getStatus());
        assertEquals(LocalDate.now(), result.getReturnDate());
        assertEquals(3, book.getAvailableCopies());
        verify(bookRepository).save(book);
        verify(issuedBookRepository).save(issue);
    }

    @Test
    void returnBookRejectsAlreadyReturnedRecord() {
        IssuedBook issue = new IssuedBook();
        issue.setBook(book);
        issue.setStatus("RETURNED");
        when(issuedBookRepository.findById(5L)).thenReturn(Optional.of(issue));

        RuntimeException error = assertThrows(RuntimeException.class,
                () -> issuedBookService.returnBook(5L));

        assertEquals("Book has already been returned", error.getMessage());
    }
}
