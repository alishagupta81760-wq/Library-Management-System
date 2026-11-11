package com.library.library.management.system.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.library.library.management.system.entity.Book;
import com.library.library.management.system.entity.Student;
import com.library.library.management.system.entity.IssuedBook;
import com.library.library.management.system.service.BookService;
import com.library.library.management.system.service.StudentService;
import com.library.library.management.system.service.IssuedBookService;

@Controller
@RequestMapping("/issued-books")
public class IssuedBookController {

    private final IssuedBookService issuedBookService;
    private final BookService bookService;
    private final StudentService studentService;

    public IssuedBookController(
            IssuedBookService issuedBookService,
            BookService bookService,
            StudentService studentService) {

        this.issuedBookService = issuedBookService;
        this.bookService = bookService;
        this.studentService = studentService;
    }

    // Show All Issued Books
    @GetMapping
    public String getAllIssuedBooks(Model model) {

        List<IssuedBook> issuedBooks =
                issuedBookService.getAllIssuedBooks();

        model.addAttribute("issuedBooks", issuedBooks);

        return "issued-books";
    }

    // Search and Filter Issued Books
    @GetMapping("/search")
    public String searchIssuedBooks(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            Model model) {

        List<IssuedBook> issuedBooks =
                issuedBookService.searchIssuedBooks(keyword, status);

        model.addAttribute("issuedBooks", issuedBooks);
        model.addAttribute("keyword", keyword);
        model.addAttribute("status", status);

        return "issued-books";
    }

    // Show Issue Book Form
    @GetMapping("/issue")
    public String showIssueBookForm(Model model) {

        List<Book> books = bookService.getAllBooks();
        List<Student> students = studentService.getAllStudents();

        model.addAttribute("books", books);
        model.addAttribute("students", students);
        model.addAttribute("today", LocalDate.now());

        return "issue-book";
    }

    // Save Issue Book
    @PostMapping("/save")
    public String issueBook(
            @RequestParam Long studentId,
            @RequestParam Long bookId,
            @RequestParam LocalDate dueDate,
            RedirectAttributes redirectAttributes) {

        try {

            issuedBookService.issueBook(studentId, bookId, dueDate);

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Book issued successfully!");

            return "redirect:/issued-books";

        } catch (RuntimeException e) {

            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    e.getMessage());

            return "redirect:/issued-books/issue";
        }
    }

    // Return Book
    @GetMapping("/return/{id}")
    public String returnBook(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        try {

            issuedBookService.returnBook(id);

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Book returned successfully!");

        } catch (RuntimeException e) {

            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    e.getMessage());
        }

        return "redirect:/issued-books";
    }
 // Show Overdue Books
    @GetMapping("/overdue")
    public String getOverdueBooks(Model model) {

        List<IssuedBook> overdueBooks =
                issuedBookService.getOverdueBooks();

        model.addAttribute("overdueBooks", overdueBooks);

        return "overdue-books";
    }
}