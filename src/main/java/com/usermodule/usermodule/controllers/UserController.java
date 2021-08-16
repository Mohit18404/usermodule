package com.usermodule.usermodule.controllers;

import com.usermodule.usermodule.enums.ResponseMessage;
import com.usermodule.usermodule.repository.UserRepository;
import com.usermodule.usermodule.requests.LoginRequest;
import com.usermodule.usermodule.requests.UserRequest;
import com.usermodule.usermodule.response.LoginResponse;
import com.usermodule.usermodule.response.SendOtpResponse;
import com.usermodule.usermodule.response.UserResponse;
import com.usermodule.usermodule.response.ValidateOtpResponse;
import com.usermodule.usermodule.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/api/user/")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @PostMapping("register")
    public UserResponse createUser(@RequestBody UserRequest userRequest) {
        UserResponse userRegisterResponse = null;
        try {
            logger.info("[UserController][createUser] the request is userRequest: {}", userRequest);
            if (userService.validateUserRequest(userRequest)) {
                userRegisterResponse = userService.createUser(userRequest);
            } else {
                userRegisterResponse.builder().code(HttpStatus.BAD_REQUEST.value()).description(ResponseMessage.BAD_REQUEST.message).build();
            }

        } catch (Exception exception) {
            userRegisterResponse.builder().code(HttpStatus.INTERNAL_SERVER_ERROR.value()).description(ResponseMessage.INTERNAL_SERVER_ERROR.message).build();
        }

        return userRegisterResponse;
    }

    @PostMapping("send/otp")
    public SendOtpResponse sendOtp(@RequestParam String loginId) {
        SendOtpResponse sendOtpResponse = null;
        try {
            logger.info("[UserController][sendOtp] the request is loginId: {}", loginId);
            if (loginId != null) {
                sendOtpResponse = userService.sendOtp(loginId);
            } else {
                sendOtpResponse.builder().code(HttpStatus.BAD_REQUEST.value()).description(ResponseMessage.BAD_REQUEST.message).build();
            }

        } catch (Exception exception) {
            sendOtpResponse.builder().code(HttpStatus.INTERNAL_SERVER_ERROR.value()).description(ResponseMessage.INTERNAL_SERVER_ERROR.message).build();
        }

        return sendOtpResponse;
    }

    @GetMapping("validate/otp")
    public ValidateOtpResponse validateOtp(@RequestParam String loginId, @RequestParam String otp) {

        ValidateOtpResponse validateOtpResponse = null;
        try {
            logger.info("[UserController][validateOtp] the request is loginId: {}", loginId);
            if (loginId != null && otp != null) {
                validateOtpResponse = userService.validateOtp(loginId, otp);
            } else {
                validateOtpResponse.builder().code(HttpStatus.BAD_REQUEST.value()).description(ResponseMessage.BAD_REQUEST.message).build();
            }
        } catch (Exception exception) {
            validateOtpResponse.builder().code(HttpStatus.INTERNAL_SERVER_ERROR.value()).description(ResponseMessage.INTERNAL_SERVER_ERROR.message).build();
        }

        return validateOtpResponse;
    }

    @PostMapping("login")
    public LoginResponse login(@RequestBody LoginRequest loginRequest) {

        LoginResponse loginResponse = null;
        try {
            if (loginResponse != null && loginRequest.getLoginId() != null && loginRequest.getPassword() != null) {
                loginResponse = userService.validateUserIdAndPassword(loginRequest.getLoginId(), loginRequest.getPassword());
            } else {
                loginResponse.builder().code(HttpStatus.BAD_REQUEST.value()).description(ResponseMessage.BAD_REQUEST.message).build();
            }
        } catch (Exception exception) {
            loginResponse.builder().code(HttpStatus.INTERNAL_SERVER_ERROR.value()).description(ResponseMessage.INTERNAL_SERVER_ERROR.message).build();
        }

        return loginResponse;
    }

}
