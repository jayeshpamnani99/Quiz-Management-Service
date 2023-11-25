package com.example.quizmanagementservice.Services;

import com.example.quizmanagementservice.CustomException;
import com.example.quizmanagementservice.Dao.QuizSubmissionDao;
import com.example.quizmanagementservice.Dao.SubmissionStatusDao;
import com.example.quizmanagementservice.Model.Constants;
import com.example.quizmanagementservice.Model.QuizSubmission;
import com.example.quizmanagementservice.Resources.QuizResource;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Indexed;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

@Service
public class QuizService {

    Logger logger = LoggerFactory.getLogger(QuizService.class);

    @Autowired
    private QuizSubmissionDao quizSubmissionDao;

    @Autowired
    private SubmissionStatusDao submissionStatusDao;

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

    public JSONObject getModuleDetailsByModuleAndCourseId(int moduleId, int courseId, String authToken) {
        final String uri = "http://" + Constants.AWS_IP + ":8083/getModuleDetailsById?courseModuleId="+moduleId+"&courseId="+courseId;

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", authToken);

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

}
