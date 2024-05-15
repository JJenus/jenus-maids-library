package com.maids.LMS.patron;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
public class PatronServiceUnitTest {

    @Mock
    private PatronRepository patronRepository;

    @InjectMocks
    private PatronService patronService;

    private final Long id  = 1L;

    @Test
    public void testGetAllPatrons() {
        // Mock data
        List<Patron> patrons = Arrays.asList(
                new Patron(id, "John Doe", "john.doe@example.com"),
                new Patron(2L, "Jane Smith", "jane.smith@example.com")
        );

        // Define behavior of mock
        when(patronRepository.findAll()).thenReturn(patrons);

        // Test the service method
        List<Patron> result = patronService.getAllPatrons();

        // Assertions
        assertEquals(2, result.size());
    }

    @Test
    public void testGetPatronByIdSuccess() {
        // Mock data
        Patron patron = new Patron(id, "John Doe", "john.doe@example.com");

        // Define behavior of mock
        when(patronRepository.findById(id)).thenReturn(Optional.of(patron));

        // Test the service method
        Patron result = patronService.getPatronById(id);

        // Assertions
        assertNotNull(result);
        assertEquals(id, result.getId());
    }

    @Test
    public void testGetPatronByIdNotFound() {
        // Define behavior of mock
        when(patronRepository.findById(id)).thenReturn(Optional.empty());

        // Test the service method
        HttpClientErrorException exception = assertThrows(HttpClientErrorException.class, () -> {
            patronService.getPatronById(id);
        });

        // Assertions
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }

    @Test
    public void testSavePatron() {
        // Mock data
        Patron patronToSave = new Patron(null, "John Doe", "john.doe@example.com");
        Patron savedPatron = new Patron(id, "John Doe", "john.doe@example.com");

        // Define behavior of mock
        when(patronRepository.save(patronToSave)).thenReturn(savedPatron);

        // Test the service method
        Patron result = patronService.addPatron(patronToSave);

        // Assertions
        assertNotNull(result);
        assertEquals(savedPatron.getId(), result.getId());
    }

    @Test
    public void testUpdatePatronSuccess() {
        // Mock data
        Patron patronToUpdate = new Patron(null, "John Doe", "john.doe@example.com");
        Patron updatedPatron = new Patron(id, "Jane Smith", "jane.smith@example.com");

        // Define behavior of mock
        when(patronRepository.existsById(id)).thenReturn(true);
        when(patronRepository.save(patronToUpdate)).thenReturn(updatedPatron);

        // Test the service method
        Patron result = patronService.updatePatron(id, patronToUpdate);

        // Assertions
        assertNotNull(result);
        assertEquals(updatedPatron.getId(), result.getId());
    }

    @Test
    public void testUpdatePatronFailed() {
        // Mock data
        Patron patronToUpdate = new Patron(null, "John Doe", "john.doe@example.com");

        // Define behavior of mock
        when(patronRepository.existsById(id)).thenReturn(false);

        // Test the service method
        HttpClientErrorException exception = assertThrows(HttpClientErrorException.class, () -> {
            patronService.updatePatron(id, patronToUpdate);
        });

        // Assertions
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }

    @Test
    public void testDeletePatron() {
        // Mock data
        doNothing().when(patronRepository).deleteById(id);

        // Test the service method
        patronService.deletePatron(id);

        // Verify that the delete method was called
        verify(patronRepository, times(1)).deleteById(id);
    }
}
