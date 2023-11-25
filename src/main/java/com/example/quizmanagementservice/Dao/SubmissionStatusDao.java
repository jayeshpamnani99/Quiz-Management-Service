package com.example.quizmanagementservice.Dao;

import com.example.quizmanagementservice.Model.SubmissionStatus;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubmissionStatusDao extends CrudRepository<SubmissionStatus, Integer> {

    public SubmissionStatus findByName(String name);
}
