package com.maids.LMS.borrowing;

import com.maids.LMS.book.Book;
import com.maids.LMS.book.BookRepository;
import com.maids.LMS.patron.Patron;
import com.maids.LMS.patron.PatronRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.HttpClientErrorException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BorrowingServiceUnitTest {
    @Mock
    private BorrowingRepository borrowingRepository;
    @Mock
    private BookRepository bookRepository;
    @Mock
    private PatronRepository patronRepository;
    @InjectMocks
    private BorrowingService borrowingService;

    @Test
    public void testBorrowBookSuccess() {
        // Mock data
        Long bookId = 1L;
        Long patronId = 1L;
        Book book = new Book(1L, "Absolute Java", "Walter Savitch", 1998, "ISBN 89784038", false);
        Patron patron = new Patron(1L, "Super Man", "s.p@example.com");

        // Define behavior of mocks
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(patronRepository.findById(patronId)).thenReturn(Optional.of(patron));
        when(borrowingRepository.save(any(BorrowingRecord.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Test the service method
        BorrowingRecord result = borrowingService.borrowBook(bookId, patronId);

        // Assertions
        assertNotNull(result);
        assertEquals(bookId, result.getBook().getId());
        assertEquals(patronId, result.getPatron().getId());
    }

    @Test
    public void testBorrowBookBookNotFound() {
        // Mock data
        Long bookId = 1L;
        Long patronId = 1L;

        // Define behavior of mocks
        when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

        // Test the service method and verify the exception
        assertThrows(HttpClientErrorException.class, () -> borrowingService.borrowBook(bookId, patronId), "Book not found");
    }

    @Test
    public void testBorrowBookPatronNotFound() {
        // Mock data
        Long bookId = 1L;
        Long patronId = 1L;
        Book book = new Book(1L, "Absolute Java", "Walter Savitch", 1998, "ISBN 89784038", false);

        // Define behavior of mocks
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(patronRepository.findById(patronId)).thenReturn(Optional.empty());

        // Test the service method and verify the exception
        assertThrows(HttpClientErrorException.class, () -> borrowingService.borrowBook(bookId, patronId), "Patron not found");
    }

    @Test
    public void testBorrowBookBookAlreadyBorrowed() {
        // Mock data
        Long bookId = 1L;
        Long patronId = 1L;
        Book book = new Book(1L, "Absolute Java", "Walter Savitch", 1998, "ISBN 89784038", true);
        Patron patron = new Patron(1L, "John Doe", "john@example.com");

        // Define behavior of mocks
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(patronRepository.findById(patronId)).thenReturn(Optional.of(patron));

        // Test the service method and verify the exception
        assertThrows(HttpClientErrorException.class, () -> borrowingService.borrowBook(bookId, patronId), "Book is already borrowed");
    }

    @Test
    public void testReturnBookSuccess() {
        // Mock data
        Long bookId = 1L;
        Long patronId = 1L;
        Book book = new Book(1L, "Absolute Java", "Walter Savitch", 1998, "ISBN 89784038", true);
        Patron patron = new Patron(1L, "Super Man", "s.p@example.com");
        BorrowingRecord borrowing = new BorrowingRecord(book, patron);

        // Define behavior of mocks
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(patronRepository.findById(patronId)).thenReturn(Optional.of(patron));
        when(borrowingRepository.findByBookAndPatron(book, patron)).thenReturn(borrowing);

        // Test the service method
        assertDoesNotThrow(() -> borrowingService.returnBook(bookId, patronId));

        // Verify that the book's status is updated and borrowing record is deleted
        assertFalse(book.isBorrowed());
        verify(borrowingRepository, times(1)).delete(borrowing);
    }
}
