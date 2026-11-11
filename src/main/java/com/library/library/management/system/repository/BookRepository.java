package com.library.library.management.system.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.library.library.management.system.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
	Page<Book> findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCaseOrIsbnContainingIgnoreCase(
	        String title, String author, String isbn, Pageable pageable);

    // Search by Title
    List<Book> findByTitleContainingIgnoreCase(String title);

    // Search by Author
    List<Book> findByAuthorContainingIgnoreCase(String author);

    // Search by ISBN
    List<Book> findByIsbnContainingIgnoreCase(String isbn);

    // Search by Title, Author or ISBN
    List<Book> findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCaseOrIsbnContainingIgnoreCase(
            String title, String author, String isbn);

    // Check duplicate ISBN while adding a new book
    boolean existsByIsbn(String isbn);

    // Check duplicate ISBN while editing (exclude current book)
    boolean existsByIsbnAndIdNot(String isbn, Long id);
}