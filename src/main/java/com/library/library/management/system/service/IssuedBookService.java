package com.library.library.management.system.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.library.library.management.system.entity.Book;
import com.library.library.management.system.entity.Student;
import com.library.library.management.system.entity.IssuedBook;

import com.library.library.management.system.repository.BookRepository;
import com.library.library.management.system.repository.StudentRepository;
import com.library.library.management.system.repository.IssuedBookRepository;


@Service
public class IssuedBookService {

    private final IssuedBookRepository issuedBookRepository;
    private final BookRepository bookRepository;
    private final StudentRepository studentRepository;

    public IssuedBookService(
            IssuedBookRepository issuedBookRepository,
            BookRepository bookRepository,
            StudentRepository studentRepository) {

        this.issuedBookRepository = issuedBookRepository;
        this.bookRepository = bookRepository;
        this.studentRepository = studentRepository;
    }

    // Issue Book
    @Transactional
    public IssuedBook issueBook(Long studentId, Long bookId, LocalDate dueDate) {

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found"));

        if (book.getAvailableCopies() <= 0) {
            throw new RuntimeException("Book is not available");
        }

        if (dueDate == null || dueDate.isBefore(LocalDate.now())) {
            throw new RuntimeException("Due date must be today or a future date");
        }

        IssuedBook issuedBook = new IssuedBook();

        issuedBook.setStudent(student);
        issuedBook.setBook(book);
        issuedBook.setIssueDate(LocalDate.now());
        issuedBook.setDueDate(dueDate);
        issuedBook.setStatus("ISSUED");

        // Decrease available copies
        book.setAvailableCopies(book.getAvailableCopies() - 1);

        bookRepository.save(book);

        return issuedBookRepository.save(issuedBook);
    }

    // Return Book
    @Transactional
    public IssuedBook returnBook(Long issueId) {

        IssuedBook issuedBook = issuedBookRepository.findById(issueId)
                .orElseThrow(() -> new RuntimeException("Issue record not found"));

        if ("RETURNED".equals(issuedBook.getStatus())) {
            throw new RuntimeException("Book has already been returned");
        }

        Book book = issuedBook.getBook();

        // Increase available copies
        book.setAvailableCopies(book.getAvailableCopies() + 1);

        issuedBook.setReturnDate(LocalDate.now());
        issuedBook.setStatus("RETURNED");

        bookRepository.save(book);

        return issuedBookRepository.save(issuedBook);
    }

    // Get All Issue Records
    public List<IssuedBook> getAllIssuedBooks() {
        return issuedBookRepository.findAll();
    }

    // Get All Issue Records with Pagination
    public Page<IssuedBook> getAllIssuedBooksPage(int page, int size) {

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by("id").descending()
        );

        return issuedBookRepository.findAll(pageable);
    }

    // Get Currently Issued Books
    public List<IssuedBook> getCurrentlyIssuedBooks() {
        return issuedBookRepository.findByStatus("ISSUED");
    }

    // Get Returned Books
    public List<IssuedBook> getReturnedBooks() {
        return issuedBookRepository.findByStatus("RETURNED");
    }

    // Count Currently Issued Books
    public long countIssuedBooks() {
        return issuedBookRepository.countByStatus("ISSUED");
    }

    // Search and Filter Issued Books (Old List Method)
    public List<IssuedBook> searchIssuedBooks(String keyword, String status) {

        if (keyword == null) {
            keyword = "";
        }

        if (status == null) {
            status = "";
        }

        keyword = keyword.trim();
        status = status.trim();

        return issuedBookRepository.searchIssuedBooks(
                keyword,
                status,
                Pageable.unpaged()
        ).getContent();
    }

    // Search and Filter Issued Books with Pagination
    public Page<IssuedBook> searchIssuedBooksPage(
            String keyword,
            String status,
            int page,
            int size) {

        if (keyword == null) {
            keyword = "";
        }

        if (status == null) {
            status = "";
        }

        keyword = keyword.trim();
        status = status.trim();

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by("id").descending()
        );

        return issuedBookRepository.searchIssuedBooks(
                keyword,
                status,
                pageable
        );
    }
 // Get Date-Wise Reports
    public List<IssuedBook> getDateWiseReport(
            LocalDate fromDate,
            LocalDate toDate,
            String reportType) {

        // Validate dates
        if (fromDate == null || toDate == null) {
            throw new IllegalArgumentException("Please select both dates");
        }

        if (fromDate.isAfter(toDate)) {
            throw new IllegalArgumentException(
                    "From date cannot be after To date");
        }

        // Validate report type
        if (reportType == null || reportType.trim().isEmpty()) {
            reportType = "ALL";
        }

        reportType = reportType.trim().toUpperCase();

        // Generate report according to type
        switch (reportType) {

            case "ISSUED":
                return issuedBookRepository.findByIssueDateBetweenAndStatus(
                        fromDate,
                        toDate,
                        "ISSUED"
                );

            case "RETURNED":
                return issuedBookRepository.findByReturnDateBetweenAndStatus(
                        fromDate,
                        toDate,
                        "RETURNED"
                );

            case "ALL":
                return issuedBookRepository.findByIssueDateBetween(
                        fromDate,
                        toDate
                );

            default:
                throw new IllegalArgumentException("Invalid report type");
        }
    }
    // Get Overdue Books
    public List<IssuedBook> getOverdueBooks() {

        return issuedBookRepository.findByStatusAndDueDateBefore(
                "ISSUED",
                LocalDate.now()
        );
    }
 // Get Recent 5 Issued Books
    public List<IssuedBook> getRecentIssuedBooks() {

        Pageable pageable = PageRequest.of(
                0,
                5,
                Sort.by("id").descending()
        );

        return issuedBookRepository.findAll(pageable).getContent();
    }
}