package com.example.quizmanagementservice.Services;

import com.example.quizmanagementservice.CustomException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class CustomExceptionTest {

    @Test
    public void testCustomExceptionCreation() {
        // Create a new CustomException object
        Exception sampleException = new Exception("Sample exception");
        HttpStatus sampleStatus = HttpStatus.BAD_REQUEST;

        CustomException customException = new CustomException(sampleException, sampleStatus);

        // Check if the CustomException object is not null
        assertNotNull(customException);

        // Validate properties of the CustomException object
        assertEquals(sampleException, customException.getE());
        assertEquals(sampleStatus, customException.getStatus());
    }

    @Test
    public void testCustomExceptionSetters() {
        // Create a new CustomException object
        CustomException customException = new CustomException(null, null);

        // Set values using setters
        Exception newException = new Exception("New exception");
        HttpStatus newStatus = HttpStatus.INTERNAL_SERVER_ERROR;

        customException.setE(newException);
        customException.setStatus(newStatus);

        // Validate updated properties using getters
        assertEquals(newException, customException.getE());
        assertEquals(newStatus, customException.getStatus());
    }
}

