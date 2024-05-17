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

    @ManyToOne(cascade = CascadeType.ALL)
    @JsonIgnore
    private Book book;

    @ManyToOne(cascade = CascadeType.ALL)
    @JsonIgnore
    private Patron patron;

    private LocalDateTime borrowDate;
    private LocalDateTime returnDate;

    public BorrowingRecord(Book book, Patron patron) {
        this.book = book;
        this.patron = patron;
    }

    @PrePersist
    public void onInsert() {
        borrowDate = LocalDateTime.now();
    }
}
