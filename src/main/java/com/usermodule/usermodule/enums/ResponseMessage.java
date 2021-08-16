package com.usermodule.usermodule.enums;

import org.springframework.http.HttpStatus;

public enum ResponseMessage {
    BAD_REQUEST("Invalid input. Please validate your inputs."),
    INTERNAL_SERVER_ERROR("Server has issues. Please try again."),
    CREATED("User successfully registered"),
    CONFLICT("User Already exist"),
    USER_NOT_FOUND("User not exist"),
    SEND_OTP("OTP send to user loginId"),
    OTP_NOT_MATCHED("OTP does not matched"),
    OTP_LIMIT_REACHED("OTP limit reached"),
    PASSWORD_NOT_MATCHED("Please enter correct password");

    public final String message;

    private ResponseMessage(String message) {
        this.message = message;
    }
}
