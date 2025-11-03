package com.utcn.userservice.handlers.exceptions.model;

import org.springframework.http.HttpStatus;

import java.util.ArrayList;

public class ConflictException extends CustomException {

    private static final String MESSAGE = "Resource conflict!";
    private static final HttpStatus httpStatus = HttpStatus.CONFLICT;

    public ConflictException(String details) {
        super(MESSAGE, httpStatus, details, new ArrayList<>());
    }
}