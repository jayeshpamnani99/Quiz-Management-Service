package com.example.quizmanagementservice.Services;

import com.example.quizmanagementservice.CustomException;
import com.example.quizmanagementservice.Dao.QuizSubmissionDao;
import com.example.quizmanagementservice.Dao.SubmissionStatusDao;
import com.example.quizmanagementservice.Model.Constants;
import com.example.quizmanagementservice.Model.QuizSubmission;
import com.example.quizmanagementservice.Resources.QuizResource;
import jakarta.persistence.EntityManager;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Indexed;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class QuizService {

    Logger logger = LoggerFactory.getLogger(QuizService.class);

    @Autowired
    private QuizSubmissionDao quizSubmissionDao;

    @Autowired
    private SubmissionStatusDao submissionStatusDao;

    @Autowired
    private EntityManager entityManager;

    public int submitQuiz(String authToken,QuizSubmission submissionData) throws Exception {
        if (null == submissionData) {
            throw new CustomException(new Exception("submission data is null"), HttpStatus.BAD_REQUEST);
        }
        if (null == submissionData.getCourseId() || submissionData.getCourseId() == 0) {
            throw new CustomException(new Exception("course id is null"), HttpStatus.BAD_REQUEST);
        }
        if (null == submissionData.getModuleId() || submissionData.getModuleId() == 0) {
            throw new CustomException(new Exception("module id is null"), HttpStatus.BAD_REQUEST);
        }
//        if (!StringUtils.hasText(submissionData.getSubmissionText())) {
//            throw new CustomException(new Exception("submissionText is null/empty"), HttpStatus.BAD_REQUEST);
//        }
        JSONObject userObj = getUserDetailsFromToken(authToken);
        if (null == userObj || userObj.isEmpty()){
            throw new CustomException(new Exception("User not Authorised"), HttpStatus.UNAUTHORIZED);
        }

        JSONObject courseModuleObj = getModuleDetailsByModuleAndCourseId(submissionData.getModuleId(),
                submissionData.getCourseId(),authToken);
        if (null == courseModuleObj || courseModuleObj.isEmpty()){
            throw new CustomException(new Exception("Course and Module with id's don't exist"), HttpStatus.NOT_FOUND);
        }

        JSONObject isQuizSubmissionPossible = isQuizSubmissionPossible(submissionData.getModuleId(),
                submissionData.getCourseId(),userObj.getInt("id"));
        if (null != isQuizSubmissionPossible && !isQuizSubmissionPossible.isEmpty()){
            if (!isQuizSubmissionPossible.getBoolean("isQuizSubmissionPossible")){
                throw new CustomException(new Exception("Quiz already submitted/graded"), HttpStatus.BAD_REQUEST);
            }
        }

        submissionData.setMarksObtained(null);
        submissionData.setStudentId(userObj.getInt("id"));

        submissionData.setSubmissionStatusId(submissionStatusDao.findByName(Constants.NOT_GRADED).getId());
        submissionData.setTotalMarks(100);

        QuizSubmission quizSubmission = quizSubmissionDao.save(submissionData);

        return quizSubmission.getId();
    }


    public JSONObject getUserDetailsFromToken(String token) {
        final String uri = "http://" + Constants.AWS_IP + ":8081/auth";

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", token);

        HttpEntity<String> entity = new HttpEntity<>(null, headers);


        try {
            ResponseEntity<String> res = restTemplate.exchange(uri, HttpMethod.GET, entity, String.class);
            String resBody = res.getBody();
            JSONObject jsonObject = new JSONObject(resBody);
            return jsonObject;
        } catch (Exception e) {
            logger.error("Error in API for auth!");
            return null;
        }
    }

    public JSONObject isQuizSubmissionPossible(int moduleId, int courseId, int studentId) throws Exception {
        if (courseId == 0 || moduleId == 0 || studentId == 0){
            throw new CustomException(new Exception("Invalid courseId/moduleId/studentId"), HttpStatus.BAD_REQUEST);
        }
        QuizSubmission quizSubmission = quizSubmissionDao.
                findQuizSubmissionByCourseIdAndAndModuleIdAndStudentIdLatest(moduleId,courseId,studentId);
        JSONObject res = new JSONObject();
        res.put("isQuizSubmissionPossible",true);
        if (quizSubmission != null && quizSubmission.getSubmissionStatusId() == 2){
            res.put("isQuizSubmissionPossible",false);
            res.put("quizSubmission",quizSubmission);
        }
        return res;
    }

    public List<QuizSubmission> getQuizSubmissionsForGrading(String authToken, int moduleId, int courseId) throws Exception {
        if (courseId == 0 || moduleId == 0 ){
            throw new CustomException(new Exception("Invalid courseId/moduleId/studentId"), HttpStatus.BAD_REQUEST);
        }

        JSONObject userObj = getUserDetailsFromToken(authToken);
        if (null == userObj || userObj.isEmpty()){
            throw new CustomException(new Exception("User not Authorised"), HttpStatus.UNAUTHORIZED);
        }
        if(userObj.getInt("roleId") != 2){
            throw new CustomException(new Exception("Students not authorised to view submissions"), HttpStatus.UNAUTHORIZED);
        }

        List<QuizSubmission> quizSubmissionList = quizSubmissionDao.
                findQuizSubmissionsByCourseIdAndAndModuleIdLatest(moduleId,courseId);

        logger.info("quizSubmissionList : {}",quizSubmissionList);

        quizSubmissionList.forEach(quizSubmission -> {
            JSONObject studentDetails = getUserDetailsFromUserId(quizSubmission.getStudentId());
            logger.info("student details : {}",studentDetails);
            if (null != studentDetails) {
                quizSubmission.setUserDetails(studentDetails.toMap());
            }
        });
        return quizSubmissionList;
    }

    public JSONObject postQuizMarks(String authToken, int submissionId, int marksObtained) throws Exception {
        if(submissionId == 0){
            throw new CustomException(new Exception("submission id is null"), HttpStatus.BAD_REQUEST);
        }

        JSONObject userObj = getUserDetailsFromToken(authToken);
        if (null == userObj || userObj.isEmpty()){
            throw new CustomException(new Exception("User not Authorised"), HttpStatus.UNAUTHORIZED);
        }
        if(userObj.getInt("roleId") != 2){
            throw new CustomException(new Exception("Students not authorised to grade"), HttpStatus.UNAUTHORIZED);
        }

        QuizSubmission alreadySubmittedData = entityManager.find(QuizSubmission.class, submissionId);
        if (null == alreadySubmittedData){
            throw new CustomException(new Exception("No submission found with id"), HttpStatus.NOT_FOUND);
        }
        if (alreadySubmittedData.getSubmissionStatusId() == 2){
            throw new CustomException(new Exception("Quiz already graded!"), HttpStatus.BAD_REQUEST);
        }
        alreadySubmittedData.setMarksObtained(marksObtained);
        alreadySubmittedData.setSubmissionStatusId(submissionStatusDao.findByName(Constants.GRADED).getId());
        QuizSubmission updatedSubmission = quizSubmissionDao.save(alreadySubmittedData);
        JSONObject res = new JSONObject();
        res.put("message","Quiz graded successfully!");
        res.put("submission",updatedSubmission);

        return res;
    }

    public JSONObject getModuleDetailsByModuleAndCourseId(int moduleId, int courseId, String authToken) {
        final String uri = "http://" + Constants.AWS_IP + ":8083/getModuleDetailsById?courseModuleId="+moduleId+"&courseId="+courseId;

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", authToken);

        logger.info("url token : {}",uri);

        HttpEntity<String> entity = new HttpEntity<>(null, headers);


        try {
            ResponseEntity<String> res = restTemplate.exchange(uri, HttpMethod.GET, entity, String.class);
            String resBody = res.getBody();
            JSONObject jsonObject = new JSONObject(resBody);
            return jsonObject;
        } catch (Exception e) {
            logger.error("Error in API for auth!");
            return null;
        }
    }

    public JSONObject getUserDetailsFromUserId(int userId) {
        final String uri = "http://" + Constants.AWS_IP + ":8081/user-details?userId="+ userId;

        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<String> entity = new HttpEntity<>(null);


        try {
            ResponseEntity<String> res = restTemplate.exchange(uri, HttpMethod.GET, entity, String.class);
            String resBody = res.getBody();
            JSONObject jsonObject = new JSONObject(resBody);
            logger.info("response body : {}",jsonObject);
            return jsonObject;
        } catch (Exception e) {
            logger.error("Error in API for getting user details from userId!");
            return null;
        }
    }

}
