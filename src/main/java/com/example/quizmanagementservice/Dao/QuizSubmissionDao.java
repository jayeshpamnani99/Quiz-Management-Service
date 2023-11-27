package com.example.quizmanagementservice.Dao;

import com.example.quizmanagementservice.Model.QuizSubmission;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuizSubmissionDao extends CrudRepository<QuizSubmission, Integer> {

    @Query(value = "select qs from QuizSubmission qs where qs.studentId=:studentId and qs.courseId=:courseId and qs.moduleId=:moduleId order by qs.id desc limit 1", nativeQuery = false)
    public QuizSubmission findQuizSubmissionByCourseIdAndAndModuleIdAndStudentIdLatest(@Param("moduleId") int moduleId, @Param("courseId") int courseId, @Param("studentId") int studentId);

    @Query(value = "select qs from QuizSubmission qs left join QuizSubmission qs2 on qs.studentId = qs2.studentId and qs.courseId = qs2.courseId and qs.moduleId = qs2.moduleId and qs.id<qs2.id where qs.courseId=:courseId and qs.moduleId=:moduleId and qs2.id is NULL AND qs.submissionStatusId = 1", nativeQuery = false)
    public List<QuizSubmission> findQuizSubmissionsByCourseIdAndAndModuleIdLatest(@Param("moduleId") int moduleId, @Param("courseId") int courseId);

}
