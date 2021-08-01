package com.usermodule.usermodule.enums;

import org.springframework.http.HttpStatus;

public enum ResponseMessage {
    BAD_REQUEST("Invalid input. Please validate your inputs."),
    INTERNAL_SERVER_ERROR("Server has issues. Please try again."),
    CONFLICT("User Already exist");

    public final String message;

    private ResponseMessage(String message) {
        this.message = message;
    }
}
