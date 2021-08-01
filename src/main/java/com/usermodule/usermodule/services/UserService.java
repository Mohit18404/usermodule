package com.usermodule.usermodule.services;

import com.usermodule.usermodule.dto.User;
import com.usermodule.usermodule.requests.UserRequest;
import com.usermodule.usermodule.response.UserRegisterResponse;

public interface UserService {
    boolean validateUserRequest(UserRequest userRequest);

    UserRegisterResponse createUser(UserRequest userRequest);

    UserRegisterResponse checkUserExists(UserRequest userRequest);

    User getUserUsingPhone(String phone);

    User getUserUsingEmail(String email);

    UserRegisterResponse getUserUsingUserId(String userId);
}
