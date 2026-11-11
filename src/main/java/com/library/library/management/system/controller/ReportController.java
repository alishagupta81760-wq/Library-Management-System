package com.library.library.management.system.controller;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.library.library.management.system.entity.IssuedBook;
import com.library.library.management.system.service.BookService;
import com.library.library.management.system.service.StudentService;
import com.library.library.management.system.service.IssuedBookService;

@Controller
public class ReportController {

    private final BookService bookService;
    private final StudentService studentService;
    private final IssuedBookService issuedBookService;

    public ReportController(
            BookService bookService,
            StudentService studentService,
            IssuedBookService issuedBookService) {

        this.bookService = bookService;
        this.studentService = studentService;
        this.issuedBookService = issuedBookService;
    }

    // Reports Dashboard and Date-Wise Reports
    @GetMapping("/reports")
    public String showReports(

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate fromDate,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate toDate,

            @RequestParam(required = false, defaultValue = "ALL")
            String reportType,

            Model model) {

        // Default date range: Last 30 days
        if (fromDate == null) {
            fromDate = LocalDate.now().minusDays(30);
        }

        if (toDate == null) {
            toDate = LocalDate.now();
        }

        // Dashboard Statistics
        model.addAttribute("totalBooks",
                bookService.countBooks());

        model.addAttribute("totalStudents",
                studentService.countStudents());

        model.addAttribute("issuedBooks",
                issuedBookService.countIssuedBooks());

        model.addAttribute("returnedBooks",
                issuedBookService.getReturnedBooks().size());

        model.addAttribute("overdueBooks",
                issuedBookService.getOverdueBooks().size());

        // Date-Wise Report
        List<IssuedBook> reportResults = Collections.emptyList();

        String reportError = null;

        if (fromDate.isAfter(toDate)) {

            reportError = "From Date cannot be after To Date.";

        } else {

            try {
                reportResults = issuedBookService.getDateWiseReport(
                        fromDate,
                        toDate,
                        reportType
                );

            } catch (IllegalArgumentException e) {
                reportError = e.getMessage();
            }
        }

        // Send data to HTML page
        model.addAttribute("reportResults", reportResults);
        model.addAttribute("fromDate", fromDate);
        model.addAttribute("toDate", toDate);
        model.addAttribute("reportType", reportType);
        model.addAttribute("reportError", reportError);

        model.addAttribute("reportCount", reportResults.size());

        return "reports";
    }
}