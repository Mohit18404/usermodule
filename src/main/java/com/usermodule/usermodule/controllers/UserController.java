package com.usermodule.usermodule.controllers;

import com.usermodule.usermodule.enums.ResponseMessage;
import com.usermodule.usermodule.requests.LogoutRequest;
import com.usermodule.usermodule.requests.SendOtpRequest;
import com.usermodule.usermodule.requests.UserRequest;
import com.usermodule.usermodule.requests.ValidateOtpRequest;
import com.usermodule.usermodule.response.LogoutResponse;
import com.usermodule.usermodule.response.SendOtpResponse;
import com.usermodule.usermodule.response.UserRegisterResponse;
import com.usermodule.usermodule.response.ValidateOtpResponse;
import com.usermodule.usermodule.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/api/user/")
public class UserController {

    @Autowired
    private UserService userService;

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @PostMapping("register")
    public UserRegisterResponse createUser(@RequestBody UserRequest userRequest) {
        UserRegisterResponse userRegisterResponse = null;
        try {
            logger.info("[UserController][createUser] the request is userRequest: {}", userRequest);
            if (!userService.validateUserRequest(userRequest)) {
                userRegisterResponse.builder().code(HttpStatus.BAD_REQUEST.value()).description(ResponseMessage.BAD_REQUEST.message).build();
            }

            userRegisterResponse = userService.createUser(userRequest);

        } catch (Exception exception) {
            userRegisterResponse.builder().code(HttpStatus.INTERNAL_SERVER_ERROR.value()).description(ResponseMessage.INTERNAL_SERVER_ERROR.message).build();
        }

        return userRegisterResponse;
    }

    @PostMapping("/v1/api/user/sendOtp")
    public SendOtpResponse newEmployee(@RequestBody SendOtpRequest sendOtpRequest) {
        //return SendOtpResponse.builder().code(HttpStatus.OK.value()).description("OTP send to the user on login Id").otp(123456).build();
        //return SendOtpResponse.builder().code(HttpStatus.FORBIDDEN.value()).description("Invalid LoginId entered").build();
        //return SendOtpResponse.builder().code(433).description("Account Blocked").build();
        //return SendOtpResponse.builder().code(533).description("Oops ! You have reached OTP limit,").build();
        //return SendOtpResponse.builder().code(1006).description("Your Session has expired,").build();
        //return SendOtpResponse.builder().code(HttpStatus.SERVICE_UNAVAILABLE.value()).description("Service Unavailable").build();
        //return SendOtpResponse.builder().code(HttpStatus.UNAUTHORIZED.value()).description("Invalid Authorization").build();
        return SendOtpResponse.builder().code(HttpStatus.BAD_REQUEST.value()).description("Please check the request").build();
    }

    @PostMapping("/v1/api/user/validateOtp")
    public ValidateOtpResponse newEmployee(@RequestBody ValidateOtpRequest validateOtpRequest) {
//        return ValidateOtpResponse.builder().code(HttpStatus.OK.value()).description("OTP validated Successfully").userId("123456")
//                .token("2345678765dfghjkjhgfd").tokenCreatedTime("457898765467").tokenExpiry("65897657").build();
        //return ValidateOtpResponse.builder().code(HttpStatus.FORBIDDEN.value()).description("Invalid Otp entered").build();
        //return ValidateOtpResponse.builder().code(433).description("Oops ! You have reached OTP limit").build();
        //return ValidateOtpResponse.builder().code(1006).description("Your Session has expired,").build();
        //return ValidateOtpResponse.builder().code(1107).description("Invalid OTP, already used OTP entered.").build();
        //return ValidateOtpResponse.builder().code(HttpStatus.SERVICE_UNAVAILABLE.value()).description("Service Unavailable").build();
        return ValidateOtpResponse.builder().code(HttpStatus.BAD_REQUEST.value()).description("Please check the request").build();
        //return ValidateOtpResponse.builder().code(HttpStatus.UNAUTHORIZED.value()).description("Invalid Authorization").build();
    }

    @PostMapping("/v1/api/user/logout")
    public LogoutResponse newEmployee(@RequestBody LogoutRequest logoutRequest) {
        //return LogoutResponse.builder().code(HttpStatus.OK.value()).description("User Logout Successfully").token("3456898765").build();
        //return LogoutResponse.builder().code(HttpStatus.FORBIDDEN.value()).description("Invalid Otp entered").build();
        //return LogoutResponse.builder().code(HttpStatus.SERVICE_UNAVAILABLE.value()).description("Service Unavailable").build();
        return LogoutResponse.builder().code(HttpStatus.BAD_REQUEST.value()).description("Please check the request").build();
        //return LogoutResponse.builder().code(HttpStatus.UNAUTHORIZED.value()).description("Invalid Authorization").build();
    }
}
