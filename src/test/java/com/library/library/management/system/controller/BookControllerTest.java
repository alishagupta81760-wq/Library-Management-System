package com.library.library.management.system.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import com.library.library.management.system.entity.Book;
import com.library.library.management.system.service.BookService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;
import org.springframework.validation.BeanPropertyBindingResult;

@ExtendWith(MockitoExtension.class)
class BookControllerTest {

    @Mock
    private BookService bookService;

    @InjectMocks
    private BookController bookController;

    @Test
    void listClampsPageAndPageSizeBeforeCallingService() {
        when(bookService.getBooksPage(0, 100)).thenReturn(new PageImpl<>(List.of()));

        String view = bookController.getAllBooks(-2, 500, new ExtendedModelMap());

        assertEquals("books", view);
        verify(bookService).getBooksPage(0, 100);
    }

    @Test
    void saveBookReturnsFormWhenIsbnAlreadyExists() {
        Book book = new Book();
        book.setIsbn("same-isbn");
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(book, "book");
        when(bookService.isIsbnExists("same-isbn")).thenReturn(true);

        String view = bookController.saveBook(book, errors);

        assertEquals("add-book", view);
        assertEquals(1, errors.getFieldErrorCount("isbn"));
        verify(bookService, never()).saveBook(book);
    }

    @Test
    void saveBookInitializesAvailableCopiesFromQuantity() {
        Book book = new Book();
        book.setIsbn("new-isbn");
        book.setQuantity(4);
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(book, "book");
        when(bookService.isIsbnExists("new-isbn")).thenReturn(false);

        String view = bookController.saveBook(book, errors);

        assertEquals("redirect:/books", view);
        assertEquals(4, book.getAvailableCopies());
        verify(bookService).saveBook(book);
    }

    @Test
    void deleteBookConfirmsItWasRemovedFromActiveCatalogue() {
        RedirectAttributesModelMap redirectAttributes = new RedirectAttributesModelMap();

        String view = bookController.deleteBook(7L, redirectAttributes);

        assertEquals("redirect:/books", view);
        assertEquals("Book removed from the active catalogue.",
                redirectAttributes.getFlashAttributes().get("successMessage"));
        verify(bookService).deleteBook(7L);
    }
}
