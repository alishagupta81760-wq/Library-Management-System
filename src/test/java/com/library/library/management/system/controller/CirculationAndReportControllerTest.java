package com.library.library.management.system.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;

import com.library.library.management.system.service.BookService;
import com.library.library.management.system.service.IssuedBookService;
import com.library.library.management.system.service.StudentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

@ExtendWith(MockitoExtension.class)
class CirculationAndReportControllerTest {

    @Mock
    private IssuedBookService issuedBookService;

    @Mock
    private BookService bookService;

    @Mock
    private StudentService studentService;

    @InjectMocks
    private IssuedBookController issuedBookController;

    @Test
    void issueBookRedirectsWithSuccessMessage() {
        RedirectAttributesModelMap flash = new RedirectAttributesModelMap();
        LocalDate dueDate = LocalDate.now().plusDays(7);

        String view = issuedBookController.issueBook(3L, 8L, dueDate, flash);

        assertEquals("redirect:/issued-books", view);
        assertEquals("Book issued successfully!", flash.getFlashAttributes().get("successMessage"));
        verify(issuedBookService).issueBook(3L, 8L, dueDate);
    }

    @Test
    void returnBookRedirectsWithServiceErrorMessage() {
        when(issuedBookService.returnBook(12L))
                .thenThrow(new RuntimeException("Issue record not found"));
        RedirectAttributesModelMap flash = new RedirectAttributesModelMap();

        String view = issuedBookController.returnBook(12L, flash);

        assertEquals("redirect:/issued-books", view);
        assertEquals("Issue record not found", flash.getFlashAttributes().get("errorMessage"));
    }

    @Test
    void reportRejectsReversedDateRangeWithoutQueryingReportService() {
        when(bookService.countBooks()).thenReturn(2L);
        when(studentService.countStudents()).thenReturn(1L);
        when(issuedBookService.countIssuedBooks()).thenReturn(1L);
        when(issuedBookService.getReturnedBooks()).thenReturn(List.of());
        when(issuedBookService.getOverdueBooks()).thenReturn(List.of());
        ReportController reportController =
                new ReportController(bookService, studentService, issuedBookService);
        LocalDate from = LocalDate.of(2026, 5, 2);
        LocalDate to = LocalDate.of(2026, 5, 1);
        ExtendedModelMap model = new ExtendedModelMap();

        String view = reportController.showReports(from, to, "ALL", model);

        assertEquals("reports", view);
        assertEquals("From Date cannot be after To Date.", model.get("reportError"));
        assertEquals(0, model.get("reportCount"));
        verify(issuedBookService, never()).getDateWiseReport(from, to, "ALL");
    }
}
