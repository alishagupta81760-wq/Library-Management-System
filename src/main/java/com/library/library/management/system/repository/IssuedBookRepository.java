package com.library.library.management.system.repository;

import java.util.List;
import java.time.LocalDate;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.library.library.management.system.entity.IssuedBook;

@Repository
public interface IssuedBookRepository extends JpaRepository<IssuedBook, Long> {

    // Find issued books by status
    List<IssuedBook> findByStatus(String status);

    // Find issued books by student ID
    List<IssuedBook> findByStudentId(Long studentId);

    // Find issued books by book ID
    List<IssuedBook> findByBookId(Long bookId);

    // Count books by status
    long countByStatus(String status);

    // Search and filter issued books with pagination
    @Query("SELECT ib FROM IssuedBook ib " +
           "WHERE (:keyword = '' OR " +
           "LOWER(ib.student.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(ib.book.title) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
           "AND (:status = '' OR ib.status = :status)")
    Page<IssuedBook> searchIssuedBooks(
            @Param("keyword") String keyword,
            @Param("status") String status,
            Pageable pageable
    );

    // Find overdue books
    List<IssuedBook> findByStatusAndDueDateBefore(
            String status,
            LocalDate date
    );

    // Find all issue records within an issue date range
    List<IssuedBook> findByIssueDateBetween(
            LocalDate fromDate,
            LocalDate toDate
    );

    // Find currently issued books within an issue date range
    List<IssuedBook> findByIssueDateBetweenAndStatus(
            LocalDate fromDate,
            LocalDate toDate,
            String status
    );

    // Find returned books within a return date range
    List<IssuedBook> findByReturnDateBetweenAndStatus(
            LocalDate fromDate,
            LocalDate toDate,
            String status
    );

}