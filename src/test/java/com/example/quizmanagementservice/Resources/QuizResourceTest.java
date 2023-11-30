package com.example.quizmanagementservice.Resources;

import com.example.quizmanagementservice.CustomException;
import com.example.quizmanagementservice.Model.QuizSubmission;
import com.example.quizmanagementservice.Resources.QuizResource;
import com.example.quizmanagementservice.Services.QuizService;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@SpringBootTest
public class QuizResourceTest {

    @Mock
    private QuizService quizService;

    @InjectMocks
    private QuizResource quizResource;

    @BeforeEach
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testSubmitQuiz() throws Exception {
        // Mocked data
        String authToken = "Bearer Token";
        QuizSubmission submissionData = new QuizSubmission();
        submissionData.setId(1);

        // Mocking service method
        when(quizService.submitQuiz(anyString(), any(QuizSubmission.class))).thenReturn(1);
        // Calling controller method
        ResponseEntity<Object> responseEntity = quizResource.submitQuiz(authToken, submissionData);


        // Validating response
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(1, ((HashMap) responseEntity.getBody()).get("id"));
        assertEquals("quiz submitted successfully!", ((HashMap) responseEntity.getBody()).get("message"));
    }

    // Add similar tests for other controller methods (isQuizSubmissionPossible, getQuizSubmissionsForGrading, postQuizMarks)

    @Test
    public void testHandleException() {
        // Mocked CustomException
        CustomException customException = new CustomException(new Exception("Sample error"), HttpStatus.BAD_REQUEST);

        // Calling exception handler method
        ResponseEntity<Object> responseEntity = quizResource.handleException(customException);

        // Validating response
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals(HttpStatus.BAD_REQUEST, ((HashMap) responseEntity.getBody()).get("status"));
        assertEquals("Sample error", ((HashMap) responseEntity.getBody()).get("message"));
    }
}
