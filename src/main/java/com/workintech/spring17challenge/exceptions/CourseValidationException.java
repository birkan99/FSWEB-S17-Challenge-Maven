package com.workintech.spring17challenge.exceptions;

import org.springframework.http.HttpStatus;

public class CourseValidationException extends ApiException {
    public CourseValidationException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}