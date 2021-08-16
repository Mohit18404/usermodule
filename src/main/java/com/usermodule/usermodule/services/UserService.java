package com.usermodule.usermodule.services;

import com.usermodule.usermodule.dto.User;
import com.usermodule.usermodule.requests.UserRequest;
import com.usermodule.usermodule.response.LoginResponse;
import com.usermodule.usermodule.response.SendOtpResponse;
import com.usermodule.usermodule.response.UserResponse;
import com.usermodule.usermodule.response.ValidateOtpResponse;

public interface UserService {
    boolean validateUserRequest(UserRequest userRequest);

    UserResponse createUser(UserRequest userRequest);

    UserResponse checkUserExists(UserRequest userRequest);

    User getUserUsingPhone(String phone);

    User getUserUsingEmail(String email);

    UserResponse getUserUsingUserId(String userId);

    SendOtpResponse sendOtp(String loginId);

    ValidateOtpResponse validateOtp(String loginId, String otp);

    ValidateOtpResponse validateOtpFromDb(int userId, String otp);

    LoginResponse validateUserIdAndPassword(String loginId, String password);
}
