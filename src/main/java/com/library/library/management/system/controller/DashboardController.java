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

        model.addAttribute("totalBooks",
                bookService.countBooks());

        model.addAttribute("totalStudents",
                studentService.countStudents());

        model.addAttribute("issuedBooks",
                issuedBookService.countIssuedBooks());

        model.addAttribute("overdueBooks",
                issuedBookService.getOverdueBooks().size());

        return "dashboard";
    }
}