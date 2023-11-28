package com.example.quizmanagementservice.Services;


import com.example.quizmanagementservice.CustomException;
import com.example.quizmanagementservice.Dao.QuizSubmissionDao;
import com.example.quizmanagementservice.Dao.SubmissionStatusDao;
import com.example.quizmanagementservice.Model.Constants;
import com.example.quizmanagementservice.Model.QuizSubmission;
import com.example.quizmanagementservice.Model.SubmissionStatus;
import jakarta.persistence.EntityManager;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@SpringBootTest
public class QuizServiceTest {

    @Mock
    private QuizSubmissionDao quizSubmissionDao;

    @Mock
    private SubmissionStatusDao submissionStatusDao;

    @Mock
    private RestTemplate restTemplate;

    @Spy
    @InjectMocks
    private QuizService quizService;

    @Mock
    private EntityManager entityManager;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void submitQuiz_Successful() throws Exception {
        // Mocking
        String authToken = "validToken";
        QuizSubmission submissionData = new QuizSubmission();
        submissionData.setCourseId(1);
        submissionData.setModuleId(1);
        submissionData.setId(0);

        JSONObject userObj = new JSONObject();
        userObj.put("id", 1);

//        when(quizService.getUserDetailsFromToken(authToken)).thenReturn(userObj);
        doReturn(userObj).when(quizService).getUserDetailsFromToken(authToken);
//        when(quizService.getModuleDetailsByModuleAndCourseId(1, 1, authToken)).thenReturn(new JSONObject());
        JSONObject obj = new JSONObject();
        obj.put("id", 1);
        doReturn(obj).when(quizService).getModuleDetailsByModuleAndCourseId(1, 1, authToken);
        when(quizSubmissionDao.save(submissionData)).thenReturn(submissionData);

        SubmissionStatus submissionStatus = new SubmissionStatus();
        submissionStatus.setId(1);
        when(submissionStatusDao.findByName(Constants.NOT_GRADED)).thenReturn(submissionStatus);

        // Test
        int result = quizService.submitQuiz(authToken, submissionData);

        assertEquals(0, result); // Assuming the ID of the saved quiz submission is 0
    }

    @Test
    void submitQuiz_NullSubmissionData() {
        // Test
        assertThrows(CustomException.class, () -> quizService.submitQuiz("validToken", null));
    }

    // Similar tests for other scenarios

//    @Test
//    void getUserDetailsFromToken_Successful() throws Exception {
//        // Mocking
//        String token = "validToken";
//        when(restTemplate.exchange(anyString(), any(), any(), eq(String.class)))
//                .thenReturn(new ResponseEntity<>("{\"id\": 1}", HttpStatus.OK));
//
//        // Test
//        JSONObject result = quizService.getUserDetailsFromToken(token);
//
//        assertNotNull(result);
//        assertEquals(1, result.getInt("id"));
//    }


    @Test
    void isQuizSubmissionPossible_Successful() throws Exception {
        // Mocking
        int moduleId = 1;
        int courseId = 1;
        int studentId = 1;

        QuizSubmission quizSubmission = new QuizSubmission();
        quizSubmission.setSubmissionStatusId(1);

//        when(quizSubmissionDao.findQuizSubmissionByCourseIdAndAndModuleIdAndStudentIdLatest(moduleId, courseId, studentId))
//                .thenReturn(quizSubmission);
        when(quizSubmissionDao.findQuizSubmissionByCourseIdAndAndModuleIdAndStudentIdLatest(moduleId, courseId, studentId))
                .thenReturn(quizSubmission);

        // Test
        JSONObject result = quizService.isQuizSubmissionPossible(moduleId, courseId, studentId);

        assertNotNull(result);
        assertTrue(result.getBoolean("isQuizSubmissionPossible"));
    }

    @Test
    void isQuizSubmissionPossible_SubmissionFound() throws Exception {
        // Mocking
        int moduleId = 1;
        int courseId = 1;
        int studentId = 1;

        QuizSubmission quizSubmission = new QuizSubmission();
        quizSubmission.setSubmissionStatusId(2);

        when(quizSubmissionDao.findQuizSubmissionByCourseIdAndAndModuleIdAndStudentIdLatest(moduleId, courseId, studentId))
                .thenReturn(quizSubmission);

        // Test
        JSONObject result = quizService.isQuizSubmissionPossible(moduleId, courseId, studentId);

        assertNotNull(result);
        assertFalse(result.getBoolean("isQuizSubmissionPossible"));
    }

    @Test
    void isQuizSubmissionPossible_InvalidInput() {
        // Test
        assertThrows(CustomException.class, () -> quizService.isQuizSubmissionPossible(0, 0, 0));
    }

    @Test
    void getQuizSubmissionsForGrading_Successful() throws Exception {
        // Mocking
        String authToken = "validToken";
        int moduleId = 1;
        int courseId = 1;

        JSONObject userObj = new JSONObject();
        userObj.put("roleId", 2);

        List<QuizSubmission> quizSubmissionList = new ArrayList<>();
        QuizSubmission quizSubmission = new QuizSubmission();
        quizSubmission.setStudentId(1);
        quizSubmissionList.add(quizSubmission);

//        when(quizService.getUserDetailsFromToken(authToken)).thenReturn(userObj);
        doReturn(userObj).when(quizService).getUserDetailsFromToken(authToken);
//        when(quizService.getModuleDetailsByModuleAndCourseId(moduleId, courseId, authToken)).thenReturn(new JSONObject()
        doReturn(new JSONObject()).when(quizService).getModuleDetailsByModuleAndCourseId(moduleId, courseId, authToken);
        when(quizSubmissionDao.findQuizSubmissionsByCourseIdAndAndModuleIdLatest(moduleId, courseId))
                .thenReturn(quizSubmissionList);

        // Test
        List<QuizSubmission> result = quizService.getQuizSubmissionsForGrading(authToken, moduleId, courseId);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void getQuizSubmissionsForGrading_InvalidCourseIdModuleId() {
        // Test
        assertThrows(CustomException.class, () -> quizService.getQuizSubmissionsForGrading("validToken", 0, 0));
    }

    @Test
    void getQuizSubmissionsForGrading_UnauthorizedRole() {
        // Mocking
        String authToken = "validToken";

        JSONObject userObj = new JSONObject();
        userObj.put("roleId", 1);

//        when(quizService.getUserDetailsFromToken(authToken)).thenReturn(userObj);
        doReturn(userObj).when(quizService).getUserDetailsFromToken(authToken);

        // Test
        assertThrows(CustomException.class, () -> quizService.getQuizSubmissionsForGrading(authToken, 1, 1));
    }


    @Test
    void postQuizMarks_Successful() throws Exception {
        // Mocking
        String authToken = "validToken";
        int submissionId = 1;
        int marksObtained = 90;

        JSONObject userObj = new JSONObject();
        userObj.put("roleId", 2);

        QuizSubmission alreadySubmittedData = new QuizSubmission();
        alreadySubmittedData.setSubmissionStatusId(1);

//        when(quizService.getUserDetailsFromToken(authToken)).thenReturn(userObj);
        doReturn(userObj).when(quizService).getUserDetailsFromToken(authToken);
        when(entityManager.find(any(), eq(submissionId))).thenReturn(alreadySubmittedData);
        when(submissionStatusDao.findByName(Constants.GRADED)).thenReturn(new SubmissionStatus());
        when(quizSubmissionDao.save(alreadySubmittedData)).thenReturn(alreadySubmittedData);

        // Test
        JSONObject result = quizService.postQuizMarks(authToken, submissionId, marksObtained);

        assertNotNull(result);
        assertEquals("Quiz graded successfully!", result.getString("message"));
        assertNull(alreadySubmittedData.getSubmissionStatusId()); // Assuming GRADED status has ID 2
        assertEquals(marksObtained, alreadySubmittedData.getMarksObtained());
    }

    @Test
    void postQuizMarks_InvalidSubmissionId() {
        // Test
        assertThrows(CustomException.class, () -> quizService.postQuizMarks("validToken", 0, 90));
    }

    @Test
    void postQuizMarks_UnauthorizedRole() {
        // Mocking
        String authToken = "validToken";
        int submissionId = 1;
        int marksObtained = 90;

        JSONObject userObj = new JSONObject();
        userObj.put("roleId", 1);

//        when(quizService.getUserDetailsFromToken(authToken)).thenReturn(userObj);
        doReturn(userObj).when(quizService).getUserDetailsFromToken(authToken);
        when(entityManager.find(any(), eq(submissionId))).thenReturn(new QuizSubmission());

        // Test
        assertThrows(CustomException.class, () -> quizService.postQuizMarks(authToken, submissionId, marksObtained));
    }

    @Test
    void postQuizMarks_SubmissionNotFound() {
        // Mocking
        String authToken = "validToken";
        int submissionId = 1;
        int marksObtained = 90;

        JSONObject userObj = new JSONObject();
        userObj.put("roleId", 2);

//        when(quizService.getUserDetailsFromToken(authToken)).thenReturn(userObj);
        doReturn(userObj).when(quizService).getUserDetailsFromToken(authToken);
        when(entityManager.find(any(), eq(submissionId))).thenReturn(null);

        // Test
        assertThrows(CustomException.class, () -> quizService.postQuizMarks(authToken, submissionId, marksObtained));
    }

    @Test
    void postQuizMarks_QuizAlreadyGraded() {
        // Mocking
        String authToken = "validToken";
        int submissionId = 1;
        int marksObtained = 90;

        JSONObject userObj = new JSONObject();
        userObj.put("roleId", 2);

        QuizSubmission alreadySubmittedData = new QuizSubmission();
        alreadySubmittedData.setSubmissionStatusId(2);

//        when(quizService.getUserDetailsFromToken(authToken)).thenReturn(userObj);
        doReturn(userObj).when(quizService).getUserDetailsFromToken(authToken);
        when(entityManager.find(any(), eq(submissionId))).thenReturn(alreadySubmittedData);

        // Test
        assertThrows(CustomException.class, () -> quizService.postQuizMarks(authToken, submissionId, marksObtained));
    }

}
