package com.maids.LMS.borrowing;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.maids.LMS.book.Book;
import com.maids.LMS.patron.Patron;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BorrowingRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Long bookId;
    private Long patronId;

    private LocalDateTime borrowDate;
    private LocalDateTime returnDate;

    public BorrowingRecord(Long book, Long patron) {
        this.bookId = book;
        this.patronId = patron;
    }

    @PrePersist
    public void onInsert() {
        borrowDate = LocalDateTime.now();
    }
}
