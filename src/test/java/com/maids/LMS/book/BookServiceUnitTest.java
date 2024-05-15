package com.maids.LMS.book;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookServiceUnitTest {
    @Mock
    private BookRepository bookRepository;
    @InjectMocks
    private BookService bookService;
    private final Long id = 1L;

    @Test
    public void testGetAllBooks() {
        // Mock data
        List<Book> books = Arrays.asList(
                new Book(1L, "Absolute Java", "Walter Savitch", 1998, "ISBN 89784038"),
                new Book(2L, "Problem Solving C++", "Walter Savitch", 1997, "ISBN 89784038")
        );

        // Define behavior of mock
        when(bookRepository.findAll()).thenReturn(books);

        // Test the service method
        List<Book> result = bookService.getAllBooks();

        // Assertions
        assertEquals(2, result.size());
    }

    @Test
    public void testGetBookByIdSuccess() {
        // Mock data
        Book book = new Book(1L, "Absolute Java", "Walter Savitch", 1998, "ISBN 89784038");

        // Define behavior of mock
        when(bookRepository.findById(id)).thenReturn(Optional.of(book));

        // Test the service method
        Book result = bookService.getBookById(id);

        // Assertions
        assertNotNull(result);
        assertEquals(id, result.getId());
    }

    @Test
    public void testGetBookByIdNotFound() {
        // Mock data

        // Define behavior of mock
        when(bookRepository.findById(id)).thenReturn(Optional.empty());

        // Test the service method
        Book result = bookService.getBookById(id);

        // Assertions
        assertNull(result);
    }

    @Test
    public void testSaveBook() {
        // Mock data
        Book bookToSave = new Book(null, "Absolute Java", "Walter Savitch", 1998, "ISBN 89784038");
        Book savedBook = new Book(1L, "Absolute Java", "Walter Savitch", 1998, "ISBN 89784038");

        // Define behavior of mock
        when(bookRepository.save(bookToSave)).thenReturn(savedBook);

        // Test the service method
        Book result = bookService.addBook(bookToSave);

        // Assertions
        assertNotNull(result);
        assertEquals(savedBook.getId(), result.getId());
    }

    @Test
    public void testUpdateBookSuccess() {
        // Mock data
        Book bookToUpdate = new Book(null, "Absolute Java", "Walter Savitch", 1998, "ISBN 89784038");
        Book savedBook = new Book(id, "Problem Solving C++", "Walter Savitch", 1998, "ISBN 89784038");

        // Define behavior of mock
        when(bookRepository.findById(id)).thenReturn(Optional.of(savedBook));
        when(bookRepository.save(bookToUpdate)).thenReturn(savedBook);

        // Test the service method
        Book result = bookService.updateBook(id, bookToUpdate);

        // Assertions
        assertNotNull(result);
        assertEquals(savedBook.getId(), result.getId());
    }

    @Test
    public void testUpdateBookFailed() {
        // Mock data
        Book bookToUpdate = new Book(null, "Absolute Java", "Walter Savitch", 1998, "ISBN 89784038");

        // Define behavior of mock
        when(bookRepository.findById(id)).thenReturn(Optional.empty());
        // Test the service method
        Book result = bookService.updateBook(id, bookToUpdate);

        // Assertions
        assertNull(result);
    }

    @Test
    public void testDeleteBook() {
        // Mock data
        Long id = 1L;
        doNothing().when(bookRepository).deleteById(id);
        // Test the service method
        bookService.deleteBook(id);

        // Verify that the delete method was called
        verify(bookRepository, times(1)).deleteById(id);
    }
}
