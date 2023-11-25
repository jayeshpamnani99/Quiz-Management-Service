package com.example.quizmanagementservice;

import org.springframework.http.HttpStatus;

public class CustomException extends Exception {

    private Exception e;
    private HttpStatus status;

    public CustomException(Exception e, HttpStatus status) {
        this.e = e;
        this.status = status;
    }

    public Exception getE() {
        return e;
    }

    public void setE(Exception e) {
        this.e = e;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public void setStatus(HttpStatus status) {
        this.status = status;
    }
}

