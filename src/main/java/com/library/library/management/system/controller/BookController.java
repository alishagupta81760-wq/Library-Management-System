package com.library.library.management.system.controller;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import com.library.library.management.system.entity.Book;
import com.library.library.management.system.service.BookService;

@Controller
@RequestMapping("/books")
public class BookController {

    private final BookService bookService;

    // Constructor Injection
    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    // Display Books with Pagination
    @GetMapping
    public String getAllBooks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {

        page = Math.max(page, 0);
        size = Math.max(1, Math.min(size, 100));

        Page<Book> books = bookService.getBooksPage(page, size);

        model.addAttribute("books", books);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", books.getTotalPages());
        model.addAttribute("totalItems", books.getTotalElements());
        model.addAttribute("pageSize", size);
        model.addAttribute("keyword", "");

        return "books";
    }

    // Show Add Book Form
    @GetMapping("/add")
    public String showAddBookForm(Model model) {
        model.addAttribute("book", new Book());
        return "add-book";
    }

    // Save Book with Validation and Duplicate ISBN Check
    @PostMapping("/save")
    public String saveBook(
            @Valid @ModelAttribute("book") Book book,
            BindingResult result) {

        if (book.getIsbn() != null
                && !book.getIsbn().isBlank()
                && bookService.isIsbnExists(book.getIsbn())) {

            result.rejectValue(
                    "isbn",
                    "duplicate",
                    "This ISBN already exists!"
            );
        }

        if (result.hasErrors()) {
            return "add-book";
        }

        book.setAvailableCopies(book.getQuantity());
        bookService.saveBook(book);

        return "redirect:/books";
    }

    // Delete Book
    @GetMapping("/delete/{id}")
    public String deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
        return "redirect:/books";
    }

    // Show Edit Book Form
    @GetMapping("/edit/{id}")
    public String showEditBookForm(
            @PathVariable Long id,
            Model model) {

        Book book = bookService.getBookById(id);
        model.addAttribute("book", book);

        return "edit-book";
    }

    // Update Book with Validation and Duplicate ISBN Check
    @PostMapping("/update/{id}")
    public String updateBook(
            @PathVariable Long id,
            @Valid @ModelAttribute("book") Book book,
            BindingResult result) {

        book.setId(id);

        if (book.getIsbn() != null
                && !book.getIsbn().isBlank()
                && bookService.isIsbnExistsForOtherBook(book.getIsbn(), id)) {

            result.rejectValue(
                    "isbn",
                    "duplicate",
                    "This ISBN already exists!"
            );
        }

        if (result.hasErrors()) {
            return "edit-book";
        }

        bookService.updateBook(id, book);

        return "redirect:/books";
    }

    // Search Books with Pagination
    @GetMapping("/search")
    public String searchBooks(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {

        page = Math.max(page, 0);
        size = Math.max(1, Math.min(size, 100));

        Page<Book> books = bookService.searchBooksPage(keyword, page, size);

        model.addAttribute("books", books);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", books.getTotalPages());
        model.addAttribute("totalItems", books.getTotalElements());
        model.addAttribute("pageSize", size);
        model.addAttribute("keyword", keyword == null ? "" : keyword);

        return "books";
    }
}