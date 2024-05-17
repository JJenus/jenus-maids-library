package com.maids.LMS.borrowing;

import com.maids.LMS.book.Book;
import com.maids.LMS.book.BookRepository;
import com.maids.LMS.patron.Patron;
import com.maids.LMS.patron.PatronRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.time.LocalDateTime;

@Service
public class BorrowingService {

    @Autowired
    private BorrowingRepository borrowingRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private PatronRepository patronRepository;

    public BorrowingRecord borrowBook(Long bookId, Long patronId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new HttpClientErrorException(HttpStatus.NOT_FOUND, "Book not found"));
        Patron patron = patronRepository.findById(patronId)
                .orElseThrow(() -> new HttpClientErrorException(HttpStatus.NOT_FOUND, "Patron not found"));


        if (book.isBorrowed()) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Book is already borrowed");
        }

        BorrowingRecord borrowing = new BorrowingRecord(book, patron);
        borrowingRepository.save(borrowing);
        book.setBorrowed(true);
        bookRepository.save(book);
        return borrowing;
    }

    public void returnBook(Long bookId, Long patronId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new HttpClientErrorException(HttpStatus.NOT_FOUND, "Book not found"));
        Patron patron = patronRepository.findById(patronId)
                .orElseThrow(() -> new HttpClientErrorException(HttpStatus.NOT_FOUND, "Patron not found"));

        if (!book.isBorrowed()) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Book is not borrowed");
        }

        BorrowingRecord borrowingRecord = borrowingRepository.findByBookAndPatronAndReturnDateIsNull(book, patron)
                .orElseThrow(() -> new HttpClientErrorException(HttpStatus.NOT_FOUND, "Borrowing record not found"));

        borrowingRecord.setReturnDate(LocalDateTime.now());
        System.out.println("ID: "+borrowingRecord.getId());
        borrowingRepository.save(borrowingRecord);

        book.setBorrowed(false);
        bookRepository.save(book);
    }
}
