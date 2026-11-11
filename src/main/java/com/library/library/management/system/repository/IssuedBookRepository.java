package com.library.library.management.system.repository;

import java.util.List;
import java.time.LocalDate;
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

    // Search and filter issued books
    @Query("SELECT ib FROM IssuedBook ib " +
           "WHERE (:keyword = '' OR " +
           "LOWER(ib.student.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(ib.book.title) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
           "AND (:status = '' OR ib.status = :status)")
    List<IssuedBook> searchIssuedBooks(
            @Param("keyword") String keyword,
            @Param("status") String status
    );
 // Find overdue books
    List<IssuedBook> findByStatusAndDueDateBefore(
            String status,
            LocalDate date
    );
}