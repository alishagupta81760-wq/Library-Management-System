package com.library.library.management.system.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.library.library.management.system.service.BookService;
import com.library.library.management.system.service.StudentService;
import com.library.library.management.system.service.IssuedBookService;

@Controller
public class DashboardController {

    private final BookService bookService;
    private final StudentService studentService;
    private final IssuedBookService issuedBookService;

    public DashboardController(
            BookService bookService,
            StudentService studentService,
            IssuedBookService issuedBookService) {

        this.bookService = bookService;
        this.studentService = studentService;
        this.issuedBookService = issuedBookService;
    }

    @GetMapping("/")
    public String dashboard(Model model) {

        // Total Books
        model.addAttribute("totalBooks",
                bookService.countBooks());

        // Total Students
        model.addAttribute("totalStudents",
                studentService.countStudents());

        // Currently Issued Books
        model.addAttribute("issuedBooks",
                issuedBookService.countIssuedBooks());

        // Returned Books
        model.addAttribute("returnedBooks",
                issuedBookService.getReturnedBooks().size());

        // Overdue Books
        model.addAttribute("overdueBooks",
                issuedBookService.getOverdueBooks().size());

        // Recent 5 Issued Books
        model.addAttribute("recentIssuedBooks",
                issuedBookService.getRecentIssuedBooks());

        return "dashboard";
    }
}