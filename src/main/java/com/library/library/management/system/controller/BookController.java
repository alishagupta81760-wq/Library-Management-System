package com.library.library.management.system.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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

    // Display All Books
    @GetMapping
    public String getAllBooks(Model model) {

        List<Book> books = bookService.getAllBooks();

        model.addAttribute("books", books);

        return "books";
    }

    // Show Add Book Form
    @GetMapping("/add")
    public String showAddBookForm(Model model) {

        model.addAttribute("book", new Book());

        return "add-book";
    }

    // Save Book
    @PostMapping("/save")
    public String saveBook(@ModelAttribute Book book) {

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
    public String showEditBookForm(@PathVariable Long id, Model model) {

        Book book = bookService.getBookById(id);

        model.addAttribute("book", book);

        return "edit-book";
    }

    // Update Book
    @PostMapping("/update/{id}")
    public String updateBook(
            @PathVariable Long id,
            @ModelAttribute Book book) {

        bookService.updateBook(id, book);

        return "redirect:/books";
    }
 // Search Books
    @GetMapping("/search")
    public String searchBooks(@RequestParam String keyword, Model model) {

        List<Book> books = bookService.searchBooks(keyword);

        model.addAttribute("books", books);
        model.addAttribute("keyword", keyword);

        return "books";
    }
}