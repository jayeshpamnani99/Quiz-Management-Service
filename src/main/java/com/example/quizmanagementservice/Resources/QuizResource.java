package com.example.quizmanagementservice.Resources;

import com.example.quizmanagementservice.CustomException;
import com.example.quizmanagementservice.Model.QuizSubmission;
import com.example.quizmanagementservice.Services.QuizService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

@RestController
public class QuizResource {
    Logger logger = LoggerFactory.getLogger(QuizResource.class);

    @Autowired
    private QuizService quizService;

    @GetMapping("/")
    public String message(){
        logger.info("getting forst log");
        return "Hello World!";
    }

    @RequestMapping(value = "submitQuiz", method = RequestMethod.POST)
    public ResponseEntity<Object> submitQuiz(@RequestHeader(HttpHeaders.AUTHORIZATION) String authToken,
                                         @RequestBody QuizSubmission submissionData) throws Exception {
        logger.info("submitting quiz");
        logger.info("{}",submissionData);
        int id = quizService.submitQuiz(authToken,submissionData);
        JSONObject obj = new JSONObject();
        obj.put("id",id);
        obj.put("message","quiz submitted successfully!");
        logger.info("obj from submit quiz : {}",obj);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(obj.toMap());
    }

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<Object> handleException(CustomException exception) {
        logger.info("inside exception handler: {}",exception);
        JSONObject obj = new JSONObject();
        obj.put("status",exception.getStatus());
        obj.put("message",exception.getE().getMessage());
        return ResponseEntity
                .status(exception.getStatus())
                .body(obj.toMap());
    }
}
