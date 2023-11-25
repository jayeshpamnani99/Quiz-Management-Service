package com.example.quizmanagementservice.Dao;

import com.example.quizmanagementservice.Model.QuizSubmission;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuizSubmissionDao extends CrudRepository<QuizSubmission, Integer> {

}
