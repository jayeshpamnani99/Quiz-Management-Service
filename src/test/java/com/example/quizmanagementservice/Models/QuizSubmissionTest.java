package com.example.quizmanagementservice.Models;

import com.example.quizmanagementservice.Model.QuizSubmission;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class QuizSubmissionTest {

    @Test
    public void testQuizSubmissionCreation() {
        // Create a new QuizSubmission object
        QuizSubmission quizSubmission = new QuizSubmission();
        quizSubmission.setId(1);
        quizSubmission.setModuleId(101);
        quizSubmission.setCourseId(201);
        quizSubmission.setStudentId(301);
        quizSubmission.setSubmissionStatusId(401);
        quizSubmission.setSubmissionText("Sample submission text");
        quizSubmission.setMarksObtained(85);
        quizSubmission.setTotalMarks(100);

        // Check if the QuizSubmission object is not null
        assertNotNull(quizSubmission);

        // Validate properties of the QuizSubmission object
        assertEquals(1, quizSubmission.getId());
        assertEquals(101, quizSubmission.getModuleId());
        assertEquals(201, quizSubmission.getCourseId());
        assertEquals(301, quizSubmission.getStudentId());
        assertEquals(401, quizSubmission.getSubmissionStatusId());
        assertEquals("Sample submission text", quizSubmission.getSubmissionText());
        assertEquals(85, quizSubmission.getMarksObtained());
        assertEquals(100, quizSubmission.getTotalMarks());
    }

    @Test
    public void testUserDetailsMapping() {
        // Create a QuizSubmission object and set user details using a Map
        QuizSubmission quizSubmission = new QuizSubmission();
        HashMap<String, String> userDetailsMap = new HashMap<>();
        userDetailsMap.put("name", "John Doe");
        userDetailsMap.put("email", "john@example.com");
        quizSubmission.setUserDetails(userDetailsMap);

        // Validate the mapping of user details
        assertEquals("John Doe", quizSubmission.getUserDetails().get("name"));
        assertEquals("john@example.com", quizSubmission.getUserDetails().get("email"));
    }

    @Test
    public void testQuizSubmissionToString() {
        // Create a QuizSubmission object and check its toString() method
        QuizSubmission quizSubmission = new QuizSubmission();
        quizSubmission.setId(2);
        quizSubmission.setModuleId(102);
        quizSubmission.setCourseId(202);
        quizSubmission.setStudentId(302);
        quizSubmission.setSubmissionStatusId(402);
        quizSubmission.setSubmissionText("Another sample submission text");
        quizSubmission.setMarksObtained(75);
        quizSubmission.setTotalMarks(90);

        String expectedToString = "QuizSubmission{id=2, moduleId=102, courseId=202, studentId=302, " +
                "submissionStatusId=402, submissionText='Another sample submission text', " +
                "marksObtained=75, totalMarks=90, userDetails=null, submissionStatus=null}";
        assertEquals(expectedToString, quizSubmission.toString());
    }
}
