package com.library.library.management.system.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;

import com.library.library.management.system.repository.BookRepository;
import com.library.library.management.system.repository.IssuedBookRepository;
import com.library.library.management.system.repository.StudentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class IssuedBookQueriesServiceTest {

    @Mock
    private IssuedBookRepository issuedBookRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private StudentRepository studentRepository;

    @InjectMocks
    private IssuedBookService issuedBookService;

    @Test
    void dateReportSelectsIssueRecordsForAllType() {
        LocalDate from = LocalDate.of(2026, 1, 1);
        LocalDate to = LocalDate.of(2026, 1, 31);
        when(issuedBookRepository.findByIssueDateBetween(from, to)).thenReturn(List.of());

        assertEquals(List.of(), issuedBookService.getDateWiseReport(from, to, "ALL"));
        verify(issuedBookRepository).findByIssueDateBetween(from, to);
    }

    @Test
    void dateReportNormalizesReturnedFilter() {
        LocalDate from = LocalDate.of(2026, 2, 1);
        LocalDate to = LocalDate.of(2026, 2, 28);
        when(issuedBookRepository.findByReturnDateBetweenAndStatus(from, to, "RETURNED"))
                .thenReturn(List.of());

        issuedBookService.getDateWiseReport(from, to, " returned ");

        verify(issuedBookRepository)
                .findByReturnDateBetweenAndStatus(from, to, "RETURNED");
    }

    @Test
    void dateReportRejectsMissingDatesAndInvalidRange() {
        assertEquals("Please select both dates",
                assertThrows(IllegalArgumentException.class,
                        () -> issuedBookService.getDateWiseReport(null, LocalDate.now(), "ALL"))
                        .getMessage());

        assertEquals("From date cannot be after To date",
                assertThrows(IllegalArgumentException.class,
                        () -> issuedBookService.getDateWiseReport(
                                LocalDate.now(), LocalDate.now().minusDays(1), "ALL"))
                        .getMessage());
    }

    @Test
    void dateReportDefaultsBlankTypeToAllAndRejectsUnknownType() {
        LocalDate from = LocalDate.of(2026, 3, 1);
        LocalDate to = LocalDate.of(2026, 3, 2);
        when(issuedBookRepository.findByIssueDateBetween(from, to)).thenReturn(List.of());

        issuedBookService.getDateWiseReport(from, to, " ");

        verify(issuedBookRepository).findByIssueDateBetween(from, to);
        assertEquals("Invalid report type",
                assertThrows(IllegalArgumentException.class,
                        () -> issuedBookService.getDateWiseReport(from, to, "MONTHLY"))
                        .getMessage());
    }

    @Test
    void overdueQueryUsesIssuedStatusAndCurrentDate() {
        when(issuedBookRepository.findByStatusAndDueDateBefore("ISSUED", LocalDate.now()))
                .thenReturn(List.of());

        issuedBookService.getOverdueBooks();

        verify(issuedBookRepository)
                .findByStatusAndDueDateBefore("ISSUED", LocalDate.now());
    }
}
