package com.usermodule.usermodule.services;

import com.usermodule.usermodule.dto.User;
import com.usermodule.usermodule.enums.ResponseMessage;
import com.usermodule.usermodule.enums.UserStatus;
import com.usermodule.usermodule.repository.UserRepository;
import com.usermodule.usermodule.requests.UserRequest;
import com.usermodule.usermodule.response.UserRegisterResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    private static final String emailRegex = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$";
    private static final Pattern emailPattern = Pattern.compile(emailRegex);
    private static final String phoneRegex = "(0|91)?[7-9][0-9]{9}";
    private static final Pattern phonePattern = Pattern.compile(phoneRegex);


    @Override
    public boolean validateUserRequest(UserRequest userRequest) {

        if (userRequest.getUserName() == null || userRequest.getEmail() == null ||
                userRequest.getPhone() == null || userRequest.getFirstName() == null || userRequest.getPassword() == null) {
            logger.info("[UserServiceImpl][validateUserRequest] the required field can not be null");
            return false;
        }

        if (!validateEmail(userRequest.getEmail())) {
            logger.info("[UserServiceImpl][validateUserRequest] the email format is wrong" + userRequest.getEmail());
            return false;
        }

        if (!validateEmail(userRequest.getPhone())) {
            logger.info("[UserServiceImpl][validateUserRequest] the phone format is wrong" + userRequest.getPhone());
            return false;
        }

        return true;
    }

    @Override
    public UserRegisterResponse createUser(UserRequest userRequest) {
        logger.info("[UserServiceImpl][createUser] create new user");
        UserRegisterResponse userRegisterResponse = checkUserExists(userRequest);
        if (userRegisterResponse.getUserId() != null) {
            return userRegisterResponse;
        } else {
            User user = mapUserEntity(userRequest);
            try {
                userRepository.save(user);
            } catch (Exception e) {
                logger.info("[UserServiceImpl][createUser] getting exception from DB : {}", e);
                userRegisterResponse = new UserRegisterResponse();
                userRegisterResponse.setCode(HttpStatus.SERVICE_UNAVAILABLE.value());
                userRegisterResponse.setDescription(ResponseMessage.INTERNAL_SERVER_ERROR.message);
            }
        }
        return userRegisterResponse;
    }

    @Override
    public UserRegisterResponse checkUserExists(UserRequest userRequest) {
        UserRegisterResponse userRegisterResponse = new UserRegisterResponse();
        User user = getUserUsingEmail(userRequest.getEmail());
        if (user != null) {
            userRegisterResponse.setCode(HttpStatus.CONFLICT.value());
            userRegisterResponse.setDescription(ResponseMessage.CONFLICT.message);
            return userRegisterResponse;
        }

        user = getUserUsingPhone(userRequest.getPhone());
        if (user != null) {
            userRegisterResponse.setCode(HttpStatus.CONFLICT.value());
            userRegisterResponse.setDescription(ResponseMessage.CONFLICT.message);
            return userRegisterResponse;
        }

        return userRegisterResponse;
    }

    @Override
    public User getUserUsingPhone(String phone) {
        User user = new User();
        try {
            user = userRepository.findByPhone(phone);
        } catch (Exception e) {
            logger.info("[UserServiceImpl][getUserUsingEmail] getting exception from DB : {}", e);
        }
        return user;
    }

    @Override
    public User getUserUsingEmail(String email) {
        User user = new User();
        try {
            user = userRepository.findByEmail(email);
        } catch (Exception e) {
            logger.info("[UserServiceImpl][getUserUsingEmail] getting exception from DB : {}", e);
        }
        return user;
    }

    @Override
    public UserRegisterResponse getUserUsingUserId(String userId) {
        return null;
    }


    public boolean validateEmail(String email) {
        Matcher matcher = emailPattern.matcher(email);
        return matcher.matches();
    }

    public boolean validatePhone(String phone) {
        Matcher matcher = phonePattern.matcher(phone);
        return matcher.matches();
    }

    public User mapUserEntity(UserRequest userRequest) {
        User user = new User();
        user.setFirstName(userRequest.getFirstName());
        user.setLastName(userRequest.getLastName());
        user.setEmail(userRequest.getEmail());
        //todo password encryption pending
        user.setPassword(userRequest.getPassword());
        user.setPhone(userRequest.getPhone());
        user.setUserStatus(UserStatus.VERIFICATION_PENDING.name());
        user.setCreatedAt(new Date());
        user.setUpdatedAt(new Date());
        return user;
    }

}
