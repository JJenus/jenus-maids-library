package com.maids.LMS.patron;

import com.maids.LMS.borrowing.BorrowingRecord;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import java.util.ArrayList;
import java.util.List;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Patron {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank(message = "Name cannot be blank")
    @Length(max = 255, message = "Name is too long")
    private String name;
    @NotBlank
    @Length(max = 255, message = "Contact information is too long")
    private String contactInformation;
    @OneToMany(cascade = CascadeType.REMOVE, orphanRemoval = true) // Cascade type is set to ALL
    private List<BorrowingRecord> borrowingRecords = new ArrayList<>();

    public Patron(Long id, String name, String contactInformation) {
        this.id = id;
        this.name = name;
        this.contactInformation = contactInformation;
        this.borrowingRecords = new ArrayList<>();
    }
}
