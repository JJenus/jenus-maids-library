package com.maids.LMS.book;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.maids.LMS.borrowing.BorrowingRepository;
import com.maids.LMS.exception.ErrorResponse;
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

@SpringBootTest
@AutoConfigureMockMvc
public class BookControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private BorrowingRepository borrowingRepository;

    @BeforeEach
    public void setup() {
        borrowingRepository.deleteAll();
        bookRepository.deleteAll();
    }

    @Test
    public void testGetAllBooks() throws Exception {
        // Populate test data
        Book book1 = bookRepository.save(new Book(null, "Absolute Java", "Walter Savitch", 1998, "ISBN 89784038", false));
        Book book2 = bookRepository.save(new Book(null, "Problem Solving C++", "Walter Savitch", 1997, "ISBN 89784038", false));

        // Send GET request
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/api/books")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        // Deserialize response
        List<Book> books = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<List<Book>>() {});

        // Assertions
        assertEquals(2, books.size());
        assertEquals(book2.getTitle(), books.get(1).getTitle());
    }

    @Test
    public void testGetBookByIdSuccess() throws Exception {
        // Populate test data
        Book book = bookRepository.save(new Book(null, "Absolute Java", "Walter Savitch", 1998, "ISBN 89784038", false));

        // Send GET request
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/api/books/{id}", book.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        // Deserialize response
        Book retrievedBook = objectMapper.readValue(result.getResponse().getContentAsString(), Book.class);

        // Assertions
        assertEquals(book.getId(), retrievedBook.getId());
    }

    @Test
    public void testGetBookByIdNotFound() throws Exception {
        // Send GET request
        mockMvc.perform(MockMvcRequestBuilders.get("/api/books/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testCreateBookSuccess() throws Exception {
        // Mock data
        Book bookToCreate = new Book(null, "New Book", "New Author", 2024, "ISBN 1234567890", false);

        // Send POST request
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookToCreate)))
                .andExpect(status().isCreated())
                .andReturn();

        // Deserialize response
        Book createdBook = objectMapper.readValue(result.getResponse().getContentAsString(), Book.class);

        // Assertions
        assertNotNull(createdBook.getId());
        assertEquals(bookToCreate.getTitle(), createdBook.getTitle());
        assertEquals(bookToCreate.getAuthor(), createdBook.getAuthor());
    }

    @Test
    public void testCreateBookInvalid() throws Exception {
        // Mock data
        Book bookToCreate = new Book(null, "", "", 2024, "ISBN 1234567890", false); // Empty values should throw bad request

        // Send POST request
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookToCreate)))
                .andExpect(status().isBadRequest())
                .andReturn();

        // Deserialize response
        ErrorResponse response = objectMapper.readValue(result.getResponse().getContentAsString(), ErrorResponse.class);
        assertNotNull(response.getMessage());
    }

    @Test
    public void testUpdateBookSuccess() throws Exception {
        // Populate test data
        Book existingBook = bookRepository.save(new Book(null, "Existing Book", "Existing Author", 2024, "ISBN 1234567890", false));

        // Mock data
        Book updatedBook = new Book(existingBook.getId(), "Updated Book", "Updated Author", 2024, "ISBN 0987654321", false);

        // Send PUT request
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.put("/api/books/{id}", existingBook.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedBook)))
                .andExpect(status().isOk())
                .andReturn();

        // Deserialize response
        Book modifiedBook = objectMapper.readValue(result.getResponse().getContentAsString(), Book.class);

        // Assertions
        assertEquals(existingBook.getId(), modifiedBook.getId());
        assertEquals(updatedBook.getTitle(), modifiedBook.getTitle());
        assertEquals(updatedBook.getAuthor(), modifiedBook.getAuthor());
    }

    @Test
    public void testUpdateBookNotFound() throws Exception {
        // Mock data
        Book updatedBook = new Book(1L, "Updated Book", "Updated Author", 2024, "ISBN 0987654321", false);

        // Send PUT request
        mockMvc.perform(MockMvcRequestBuilders.put("/api/books/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedBook)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testDeleteBook() throws Exception {
        // Populate test data
        Book existingBook = bookRepository.save(new Book(null, "Existing Book", "Existing Author", 2024, "ISBN 1234567890", false));

        // Send DELETE request
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/books/{id}", existingBook.getId()))
                .andExpect(status().isOk());

        // Verify that the book was deleted
        assertFalse(bookRepository.existsById(existingBook.getId()));
    }
}

