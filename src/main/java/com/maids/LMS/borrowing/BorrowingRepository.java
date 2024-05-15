package com.maids.LMS.borrowing;

import com.maids.LMS.book.Book;
import com.maids.LMS.patron.Patron;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BorrowingRepository extends JpaRepository<BorrowingRecord, Long> {
    BorrowingRecord findByBookAndPatron(Book book, Patron patron);
}
