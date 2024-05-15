package com.maids.LMS.borrowing;

import com.maids.LMS.book.Book;
import com.maids.LMS.book.BookRepository;
import com.maids.LMS.patron.Patron;
import com.maids.LMS.patron.PatronRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

@Service
public class BorrowingService {

    @Autowired
    private BorrowingRepository borrowingRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private PatronRepository patronRepository;

    public BorrowingRecord borrowBook(Long bookId, Long patronId) {
        Book book = bookRepository.findById(bookId).orElse(null);
        Patron patron = patronRepository.findById(patronId).orElse(null);

        if (book == null) {
            throw new HttpClientErrorException(HttpStatus.NOT_FOUND, "Book not found");
        }
        if (patron == null) {
            throw new HttpClientErrorException(HttpStatus.NOT_FOUND, "Patron not found");
        }
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
        Book book = bookRepository.findById(bookId).orElse(null);
        Patron patron = patronRepository.findById(patronId).orElse(null);

        if (book == null) {
            throw new HttpClientErrorException(HttpStatus.NOT_FOUND, "Book not found");
        }
        if (patron == null) {
            throw new HttpClientErrorException(HttpStatus.NOT_FOUND, "Patron not found");
        }
        if (!book.isBorrowed()) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Book is not borrowed");
        }

        BorrowingRecord borrowing = borrowingRepository.findByBookAndPatron(book, patron);
        borrowingRepository.delete(borrowing);
        book.setBorrowed(false);
        bookRepository.save(book);
    }
}
