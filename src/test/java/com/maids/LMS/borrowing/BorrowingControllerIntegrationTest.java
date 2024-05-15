package com.maids.LMS.borrowing;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.maids.LMS.book.Book;
import com.maids.LMS.book.BookRepository;
import com.maids.LMS.patron.Patron;
import com.maids.LMS.patron.PatronRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@SpringBootTest
@AutoConfigureMockMvc
public class BorrowingControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private PatronRepository patronRepository;

    @Autowired
    private BorrowingRepository borrowingRepository;

    @BeforeEach
    public void setup() {
        borrowingRepository.deleteAll();
        bookRepository.deleteAll();
        patronRepository.deleteAll();
    }

    @Test
    public void testBorrowBook() throws Exception {
        // Populate test data
        Book book = bookRepository.save(new Book(null, "Borrowable Book", "Author", 2023, "ISBN-12345", false));
        Patron patron = patronRepository.save(new Patron(null, "John Doe", "john@example.com"));

        // Send POST request
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/borrow/{bookId}/patron/{patronId}", book.getId(), patron.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        // Deserialize response
        BorrowingRecord borrowingRecord = objectMapper.readValue(result.getResponse().getContentAsString(), BorrowingRecord.class);

        // Assertions
        assertNotNull(borrowingRecord);
        assertEquals(book.getId(), borrowingRecord.getBook().getId());
        assertEquals(patron.getId(), borrowingRecord.getPatron().getId());
        assertNotNull(borrowingRecord.getBorrowDate());
    }

    @Test
    public void testBorrowBookNotFound() throws Exception {
        // Populate test data
        Patron patron = patronRepository.save(new Patron(null, "John Doe", "john@example.com"));

        // Send POST request with invalid bookId
        mockMvc.perform(MockMvcRequestBuilders.post("/api/borrow/{bookId}/patron/{patronId}", 1L, patron.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testBorrowBookInvalidPatron() throws Exception {
        // Populate test data
        Book book = bookRepository.save(new Book(null, "Borrowable Book", "Author", 2023, "ISBN-12345", false));

        // Send POST request with invalid patronId
        mockMvc.perform(MockMvcRequestBuilders.post("/api/borrow/{bookId}/patron/{patronId}", book.getId(), 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void testReturnBook() throws Exception {
        // Populate test data
        Book book = bookRepository.save(new Book(null, "Returnable Book", "Author", 2023, "ISBN-12345", true));
        Patron patron = patronRepository.save(new Patron(null, "John Doe", "john@example.com"));
        BorrowingRecord borrowingRecord = borrowingRepository.save(new BorrowingRecord(null, book, patron, LocalDateTime.now(), null));

        // Send PUT request
        mockMvc.perform(MockMvcRequestBuilders.put("/api/return/{bookId}/patron/{patronId}", book.getId(), patron.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // Verify that the return date was set
        BorrowingRecord updatedRecord = borrowingRepository.findById(borrowingRecord.getId()).orElse(null);
        assertNotNull(updatedRecord);
        assertNotNull(updatedRecord.getReturnDate());
    }

    @Test
    public void testReturnBookNotFound() throws Exception {
        // Send PUT request with invalid bookId and patronId
        mockMvc.perform(MockMvcRequestBuilders.put("/api/return/{bookId}/patron/{patronId}", 1L, 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}

