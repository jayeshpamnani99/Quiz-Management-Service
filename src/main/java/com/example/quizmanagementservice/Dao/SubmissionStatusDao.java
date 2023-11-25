package com.example.quizmanagementservice.Dao;

import com.example.quizmanagementservice.Model.SubmissionStatus;
import org.springframework.data.repository.CrudRepository;

public interface SubmissionStatusDao extends CrudRepository<SubmissionStatus, Integer> {
}
