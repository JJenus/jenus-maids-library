package com.maids.LMS.borrowing;

import com.maids.LMS.book.Book;
import com.maids.LMS.patron.Patron;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BorrowingRepository extends JpaRepository<BorrowingRecord, Long> {
    Optional<BorrowingRecord> findByBookAndPatron(Book book, Patron patron);
}
