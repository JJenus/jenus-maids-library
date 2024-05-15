package com.maids.LMS.book;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

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
                new Book(1L, "Absolute Java", "Walter Savitch", 1998, "ISBN 89784038", false),
                new Book(2L, "Problem Solving C++", "Walter Savitch", 1997, "ISBN 89784038", false)
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
        Book book = new Book(1L, "Absolute Java", "Walter Savitch", 1998, "ISBN 89784038", false);

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
        // Define behavior of mock
        when(bookRepository.findById(id)).thenReturn(Optional.empty());

        // Test the service method
        HttpClientErrorException exception = assertThrows(HttpClientErrorException.class, () -> {
            bookService.getBookById(id);
        });

        // Assertions
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }

    @Test
    public void testSaveBook() {
        // Mock data
        Book bookToSave = new Book(null, "Absolute Java", "Walter Savitch", 1998, "ISBN 89784038", false);
        Book savedBook = new Book(1L, "Absolute Java", "Walter Savitch", 1998, "ISBN 89784038", false);

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
        Book bookToUpdate = new Book(null, "Absolute Java", "Walter Savitch", 1998, "ISBN 89784038", false);
        Book updatedBook = new Book(id, "Problem Solving C++", "Walter Savitch", 1998, "ISBN 89784038", false);

        // Define behavior of mock
        when(bookRepository.existsById(id)).thenReturn(true);
        when(bookRepository.save(bookToUpdate)).thenReturn(updatedBook);

        // Test the service method
        Book result = bookService.updateBook(id, bookToUpdate);

        // Assertions
        assertNotNull(result);
        assertEquals(updatedBook.getId(), result.getId());
    }

    @Test
    public void testUpdateBookFailed() {
        // Mock data
        Book bookToUpdate = new Book(null, "Absolute Java", "Walter Savitch", 1998, "ISBN 89784038", false);

        // Define behavior of mock
        when(bookRepository.existsById(id)).thenReturn(false);

        // Test the service method
        HttpClientErrorException exception = assertThrows(HttpClientErrorException.class, () -> {
            bookService.updateBook(id, bookToUpdate);
        });

        // Assertions
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
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
