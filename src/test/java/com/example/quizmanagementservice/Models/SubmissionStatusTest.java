package com.example.quizmanagementservice.Models;

import com.example.quizmanagementservice.Model.SubmissionStatus;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class SubmissionStatusTest {

    @Test
    public void testSubmissionStatusCreation() {
        // Create a new SubmissionStatus object
        SubmissionStatus submissionStatus = new SubmissionStatus();
        submissionStatus.setId(1);
        submissionStatus.setName("Submitted");

        // Check if the SubmissionStatus object is not null
        assertNotNull(submissionStatus);

        // Validate properties of the SubmissionStatus object
        assertEquals(1, submissionStatus.getId());
        assertEquals("Submitted", submissionStatus.getName());
    }

    @Test
    public void testSubmissionStatusToString() {
        // Create a SubmissionStatus object and check its toString() method
        SubmissionStatus submissionStatus = new SubmissionStatus();
        submissionStatus.setId(2);
        submissionStatus.setName("In Progress");

        String expectedToString = "SubmissionStatus{id=2, name='In Progress'}";
        assertEquals(expectedToString, submissionStatus.toString());
    }
}
