package com.maids.LMS.book;

import com.maids.LMS.borrowing.BorrowingRecord;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Title cannot be blank")
    @Length(max = 255, message = "Title is too long")
    private String title;

    @NotBlank(message = "Author cannot be blank")
    @Length(max = 255, message = "Author is too long")
    private String author;

    @NotNull(message = "Publication year cannot be null")
    @Positive(message = "Publication year must be a positive number")
    private int publicationYear;

    @NotBlank(message = "ISBN cannot be blank")
    @Length(max = 255, message = "ISBN is too long")
    private String isbn;

    @NotNull(message = "Borrowed status cannot be null")
    private boolean borrowed;

    @OneToMany( cascade = CascadeType.REMOVE, orphanRemoval = true) // Cascade type is set to ALL
    private List<BorrowingRecord> borrowingRecords = new ArrayList<>();

    public Book(Long id, String title, String author, int publicationYear, String isbn, boolean borrowed) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.publicationYear = publicationYear;
        this.isbn = isbn;
        this.borrowed = borrowed;
        this.borrowingRecords = new ArrayList<>();
    }
}
