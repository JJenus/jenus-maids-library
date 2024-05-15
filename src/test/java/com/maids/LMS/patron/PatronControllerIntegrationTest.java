package com.maids.LMS.patron;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import com.maids.LMS.borrowing.BorrowingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
public class PatronControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PatronRepository patronRepository;
    @Autowired
    private BorrowingRepository borrowingRepository;

    @BeforeEach
    public void setup() {
        borrowingRepository.deleteAll();
        patronRepository.deleteAll();
    }

    @Test
    public void testGetAllPatrons() throws Exception {
        // Populate test data
        Patron patron1 = patronRepository.save(new Patron(null, "Walter Savitch", "walter@example.com"));
        Patron patron2 = patronRepository.save(new Patron(null, "Jane Smith", "jane@example.com"));

        // Send GET request
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/api/patrons")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        // Deserialize response
        List<Patron> patrons = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<List<Patron>>() {});

        // Assertions
        assertEquals(2, patrons.size());
    }

    @Test
    public void testGetPatronByIdSuccess() throws Exception {
        // Populate test data
        Patron patron = patronRepository.save(new Patron(null, "Walter Savitch", "walter@example.com"));

        // Send GET request
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/api/patrons/{id}", patron.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        // Deserialize response
        Patron retrievedPatron = objectMapper.readValue(result.getResponse().getContentAsString(), Patron.class);

        // Assertions
        assertEquals(patron.getId(), retrievedPatron.getId());
    }

    @Test
    public void testGetPatronByIdNotFound() throws Exception {
        // Send GET request
        mockMvc.perform(MockMvcRequestBuilders.get("/api/patrons/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testCreatePatronSuccess() throws Exception {
        // Mock data
        Patron patronToCreate = new Patron(null, "New Patron", "new@example.com");

        // Send POST request
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/patrons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(patronToCreate)))
                .andExpect(status().isCreated())
                .andReturn();

        // Deserialize response
        Patron createdPatron = objectMapper.readValue(result.getResponse().getContentAsString(), Patron.class);

        // Assertions
        assertNotNull(createdPatron.getId());
        assertEquals(patronToCreate.getName(), createdPatron.getName());
        assertEquals(patronToCreate.getContactInformation(), createdPatron.getContactInformation());
    }

    @Test
    public void testCreatePatronInvalid() throws Exception {
        // Mock data with invalid email
        Patron patronToCreate = new Patron(null, "", ""); //empty should be a bad request

        // Send POST request
        mockMvc.perform(MockMvcRequestBuilders.post("/api/patrons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(patronToCreate)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testUpdatePatronSuccess() throws Exception {
        // Populate test data
        Patron existingPatron = patronRepository.save(new Patron(null, "Existing Patron", "existing@example.com"));

        // Mock data
        Patron updatedPatron = new Patron(existingPatron.getId(), "Updated Patron", "updated@example.com");

        // Send PUT request
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.put("/api/patrons/{id}", existingPatron.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedPatron)))
                .andExpect(status().isOk())
                .andReturn();

        // Deserialize response
        Patron modifiedPatron = objectMapper.readValue(result.getResponse().getContentAsString(), Patron.class);

        // Assertions
        assertEquals(existingPatron.getId(), modifiedPatron.getId());
        assertEquals(updatedPatron.getName(), modifiedPatron.getName());
        assertEquals(updatedPatron.getContactInformation(), modifiedPatron.getContactInformation());
    }

    @Test
    public void testUpdatePatronNotFound() throws Exception {
        // Mock data
        Patron updatedPatron = new Patron(1L, "Updated Patron", "updated@example.com");

        // Send PUT request
        mockMvc.perform(MockMvcRequestBuilders.put("/api/patrons/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedPatron)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testDeletePatron() throws Exception {
        // Populate test data
        Patron existingPatron = patronRepository.save(new Patron(null, "Existing Patron", "existing@example.com"));

        // Send DELETE request
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/patrons/{id}", existingPatron.getId()))
                .andExpect(status().isOk());

        // Verify that the patron was deleted
        assertFalse(patronRepository.existsById(existingPatron.getId()));
    }
}
