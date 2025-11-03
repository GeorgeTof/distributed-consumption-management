package com.utcn.deviceservice.handlers.exceptions.model;

import org.springframework.http.HttpStatus;

import java.util.ArrayList;

public class BadRequestException extends CustomException {
    private static final String MESSAGE = "Bad request!";
    private static final HttpStatus httpStatus = HttpStatus.BAD_REQUEST;

    public BadRequestException(String details) {
        super(MESSAGE, httpStatus, details, new ArrayList<>());
    }
}