package com.example.quizmanagementservice.Model;

import jakarta.persistence.*;

import java.util.HashMap;
import java.util.Map;

@Entity
@Table(name="quiz_submission")
public class QuizSubmission
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer moduleId;
    private Integer courseId;
    private Integer studentId;

    private Integer submissionStatusId;

    private String submissionText;
    private Integer marksObtained;
    private Integer totalMarks;

    @Transient
    private HashMap userDetails;

    @Transient
    private SubmissionStatus submissionStatus;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getModuleId() {
        return moduleId;
    }

    public void setModuleId(Integer moduleId) {
        this.moduleId = moduleId;
    }

    public Integer getCourseId() {
        return courseId;
    }

    public void setCourseId(Integer courseId) {
        this.courseId = courseId;
    }

    public Integer getStudentId() {
        return studentId;
    }

    public void setStudentId(Integer studentId) {
        this.studentId = studentId;
    }

    public Integer getSubmissionStatusId() {
        return submissionStatusId;
    }

    public void setSubmissionStatusId(Integer submissionStatusId) {
        this.submissionStatusId = submissionStatusId;
    }

    public String getSubmissionText() {
        return submissionText;
    }

    public void setSubmissionText(String submissionText) {
        this.submissionText = submissionText;
    }


    public Integer getMarksObtained() {
        return marksObtained;
    }

    public void setMarksObtained(Integer marksObtained) {
        this.marksObtained = marksObtained;
    }

    public Integer getTotalMarks() {
        return totalMarks;
    }

    public void setTotalMarks(Integer totalMarks) {
        this.totalMarks = totalMarks;
    }

    public SubmissionStatus getSubmissionStatus() {
        return submissionStatus;
    }

    public void setSubmissionStatus(SubmissionStatus submissionStatus) {
        this.submissionStatus = submissionStatus;
    }

    public Map getUserDetails() {
        return userDetails;
    }

    public void setUserDetails(Map userDetails) {
        this.userDetails = new HashMap(userDetails);
    }

    @Override
    public String toString() {
        return "QuizSubmission{" +
                "id=" + id +
                ", moduleId=" + moduleId +
                ", courseId=" + courseId +
                ", studentId=" + studentId +
                ", submissionStatusId=" + submissionStatusId +
                ", submissionText='" + submissionText + '\'' +
                ", marksObtained=" + marksObtained +
                ", totalMarks=" + totalMarks +
                ", userDetails=" + userDetails +
                ", submissionStatus=" + submissionStatus +
                '}';
    }
}
