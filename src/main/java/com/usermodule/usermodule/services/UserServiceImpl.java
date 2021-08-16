package com.usermodule.usermodule.services;

import com.usermodule.usermodule.dto.Otp;
import com.usermodule.usermodule.dto.Token;
import com.usermodule.usermodule.dto.User;
import com.usermodule.usermodule.enums.OtpStatus;
import com.usermodule.usermodule.enums.ResponseMessage;
import com.usermodule.usermodule.enums.TokenStatus;
import com.usermodule.usermodule.enums.UserStatus;
import com.usermodule.usermodule.repository.OtpRepository;
import com.usermodule.usermodule.repository.TokenRepository;
import com.usermodule.usermodule.repository.UserRepository;
import com.usermodule.usermodule.requests.UserRequest;
import com.usermodule.usermodule.response.LoginResponse;
import com.usermodule.usermodule.response.SendOtpResponse;
import com.usermodule.usermodule.response.UserResponse;
import com.usermodule.usermodule.response.ValidateOtpResponse;
import com.usermodule.usermodule.utils.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OtpRepository otpRepository;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private MyUserDetailsService myUserDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private TokenRepository tokenRepository;

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    private static final String emailRegex = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$";
    private static final Pattern emailPattern = Pattern.compile(emailRegex);
    private static final String phoneRegex = "(0|91)?[7-9][0-9]{9}";
    private static final Pattern phonePattern = Pattern.compile(phoneRegex);


    @Override
    public boolean validateUserRequest(UserRequest userRequest) {

        if (userRequest == null || userRequest.getUserName() == null || userRequest.getEmail() == null ||
                userRequest.getPhone() == null || userRequest.getFirstName() == null || userRequest.getPassword() == null) {
            logger.info("[UserServiceImpl][validateUserRequest] the required field can not be null");
            return false;
        }

        if (!validateEmail(userRequest.getEmail())) {
            logger.info("[UserServiceImpl][validateUserRequest] the email format is wrong" + userRequest.getEmail());
            return false;
        }

        if (!validatePhone(userRequest.getPhone())) {
            logger.info("[UserServiceImpl][validateUserRequest] the phone format is wrong" + userRequest.getPhone());
            return false;
        }

        return true;
    }

    @Override
    public UserResponse createUser(UserRequest userRequest) {
        logger.info("[UserServiceImpl][createUser] create new user");
        UserResponse userRegisterResponse = checkUserExists(userRequest);
        if (userRegisterResponse.getUserId() != null) {
            return userRegisterResponse;
        } else {
            User user = mapUserEntity(userRequest);
            try {
                User userResponse = userRepository.save(user);
                userRegisterResponse.setUserId(userResponse.getUserId());
                userRegisterResponse.setCode(HttpStatus.CREATED.value());
                userRegisterResponse.setDescription(ResponseMessage.CREATED.message);
            } catch (Exception e) {
                logger.info("[UserServiceImpl][createUser] getting exception from DB : {}", e);
                userRegisterResponse = new UserResponse();
                userRegisterResponse.setCode(HttpStatus.SERVICE_UNAVAILABLE.value());
                userRegisterResponse.setDescription(ResponseMessage.INTERNAL_SERVER_ERROR.message);
            }
        }
        return userRegisterResponse;
    }

    @Override
    public SendOtpResponse sendOtp(String loginId) {
        SendOtpResponse sendOtpResponse = new SendOtpResponse();
        sendOtpResponse.setLoginId(loginId);
        try {
            if (loginId != null && loginId.contains("@") && validateEmail(loginId) && (getUserUsingEmail(loginId).getUserId() != 0)) {
                sendOtpOnEmail(loginId);
                sendOtpResponse.builder().code(HttpStatus.OK.value()).description(ResponseMessage.SEND_OTP.message).build();
            } else if (loginId != null && validatePhone(loginId) && (getUserUsingEmail(loginId).getUserId() != 0)) {
                sendOtpOnPhone(loginId);
                sendOtpResponse.builder().code(HttpStatus.OK.value()).description(ResponseMessage.SEND_OTP.message).build();
            } else {
                sendOtpResponse = sendOtpResponse.builder().code(HttpStatus.BAD_REQUEST.value()).description(ResponseMessage.BAD_REQUEST.message).build();
            }
        } catch (Exception e) {
            logger.info("[UserServiceImpl][createUser] getting exception from DB : {}", e);
            sendOtpResponse.builder().code(HttpStatus.SERVICE_UNAVAILABLE.value()).description(ResponseMessage.INTERNAL_SERVER_ERROR.message).build();
        }
        return sendOtpResponse;
    }

    @Override
    public ValidateOtpResponse validateOtp(String loginId, String otp) {
        ValidateOtpResponse validateOtpResponse = null;
        User user = null;
        if (loginId.contains("@") && validateEmail(loginId)) {
            user = getUserUsingEmail(loginId);
        } else if (validatePhone(loginId)) {
            user = getUserUsingPhone(loginId);
        }
        if (user.getUserId() == 0) {
            validateOtpResponse = validateOtpResponse.builder().code(HttpStatus.BAD_REQUEST.value()).description(ResponseMessage.USER_NOT_FOUND.message).build();
        } else {
            validateOtpResponse = validateOtpFromDb(user.getUserId(), otp);
        }
        return validateOtpResponse;
    }

    @Override
    public UserResponse checkUserExists(UserRequest userRequest) {
        UserResponse userResponse = new UserResponse();
        User user = getUserUsingEmail(userRequest.getEmail());
        if (user != null) {
            userResponse.setCode(HttpStatus.CONFLICT.value());
            userResponse.setDescription(ResponseMessage.CONFLICT.message);
            return userResponse;
        }

        user = getUserUsingPhone(userRequest.getPhone());
        if (user != null) {
            userResponse.setCode(HttpStatus.CONFLICT.value());
            userResponse.setDescription(ResponseMessage.CONFLICT.message);
            return userResponse;
        }

        return userResponse;
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
    public UserResponse getUserUsingUserId(String userId) {
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
        user.setCreatedAt(setCurrentDateAndTime());
        user.setUpdatedAt(setCurrentDateAndTime());
        return user;
    }

    public Otp sendOtpOnEmail(String email) {
        Otp otp = new Otp();
        otp.setUserId(1);
        otp.setOtp("123456");
        otp.setCreatedAt(setCurrentDateAndTime());
        otp.setUpdatedAt(setCurrentDateAndTime());
        otp.setRetry(0);
        otp.setStatus(OtpStatus.VERIFICATION_PENDING.toString());
        try {
            otp = otpRepository.save(otp);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return otp;
    }

    public Otp sendOtpOnPhone(String phone) {
        Otp otp = new Otp();
        otp.setUserId(1);
        otp.setOtp("123456");
        otp.setCreatedAt(setCurrentDateAndTime());
        otp.setUpdatedAt(setCurrentDateAndTime());
        otp.setRetry(0);
        otp.setStatus(OtpStatus.VERIFICATION_PENDING.toString());
        try {
            otp = otpRepository.save(otp);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return otp;
    }


    private Date setCurrentDateAndTime() {
        Date today = Calendar.getInstance().getTime();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        String currentTime = format.format(today);
        Date date = null;
        try {
            date = format.parse(currentTime);
        } catch (Exception e) {
            e.getStackTrace();
        }
        return date;
    }

    @Override
    public ValidateOtpResponse validateOtpFromDb(int userId, String otp) {
        ValidateOtpResponse validateOtpResponse = null;
        try {
            List<Otp> otpResponse = otpRepository.findByUserId(userId);
            if (otpResponse != null) {
                Otp otpData = findLatestOtp(otpResponse);
                if (otpData.getStatus().equals(OtpStatus.VERIFICATION_PENDING.toString()) && (otpData.getRetry() < 3)) {
                    if (otpData.getOtp().equals(otp)) {
                        String token = createToken(String.valueOf(userId), otpData.getOtp());
                        Token tokenResponse = saveFreshTokenInDatabase(token, userId);
                        validateOtpResponse = mapValidateOtpResponse(token, userId, tokenResponse.getCreatedAt());
                    } else {
                        otpData = updateOtpData(otpData);
                        validateOtpResponse = mapValidateOtpResponse(otpData.getUserId(), ResponseMessage.OTP_NOT_MATCHED.message);
                    }
                } else
                    validateOtpResponse = mapValidateOtpResponse(userId, ResponseMessage.OTP_LIMIT_REACHED.message);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return validateOtpResponse;
    }

    @Override
    public LoginResponse validateUserIdAndPassword(String loginId, String password) {
        User user = null;
        LoginResponse loginResponse = null;
        if (loginId != null && password != null) {
            if (isUserIdEmail(loginId)) {
                user = userRepository.findByEmail(loginId);
            } else {
                user = userRepository.findByPhone(loginId);
            }
            loginResponse = loginViaUserIdPassword(user, password);
        }

        return loginResponse;
    }

    private LoginResponse loginViaUserIdPassword(User user, String password) {
        LoginResponse loginResponse = null;
        if (user != null && password != null) {
            if (password.equals(user.getPassword())) {
                String token = createToken(String.valueOf(user.getUserId()), password);
                List<Token> tokenList = tokenRepository.findByUserId(user.getUserId());
                tokenList.forEach(tokenData -> tokenData.setStatus(TokenStatus.EXPIRED.name()));
                Token tokenData = setTokenValue(token, user);
                tokenList.add(tokenData);
                tokenRepository.saveAll(tokenList);
                loginResponse = loginResponse(token, user.getUserId(), HttpStatus.OK.value(), HttpStatus.OK.name(), setCurrentDateAndTime());
            } else {
                loginResponse = loginResponse(null, user.getUserId(), HttpStatus.BAD_REQUEST.value(), ResponseMessage.PASSWORD_NOT_MATCHED.message, null);
            }
        }
        return loginResponse;
    }

    private LoginResponse loginResponse(String token, int userId, int code, String description, Date date) {
        LoginResponse loginResponse = LoginResponse.builder().code(code).description(description)
                .userId(userId).token(token).tokenCreatedTime(date).build();
        return loginResponse;
    }

    private Token setTokenValue(String token, User user) {
        Token tokenData = new Token();
        tokenData.setToken(token);
        tokenData.setUserId(user.getUserId());
        tokenData.setStatus(TokenStatus.ACTIVE.name());
        tokenData.setCreatedAt(setCurrentDateAndTime());
        tokenData.setUpdatedAt(setCurrentDateAndTime());
        return tokenData;
    }

    private Otp updateOtpData(Otp otp) {
        otp.setRetry(otp.getRetry() + 1);
        if (otp.getRetry() == 3) {
            otp.setStatus(OtpStatus.OTP_EXPIRED.toString());
        }
        otp = otpRepository.save(otp);
        return otp;
    }

    private ValidateOtpResponse mapValidateOtpResponse(int userId, String responseMessage) {
        return ValidateOtpResponse.builder().code(HttpStatus.OK.value()).description(responseMessage)
                .userId(userId).build();
    }


    public String createToken(String userId, String password) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userId, password));
        } catch (Exception e) {
            e.printStackTrace();
        }
        final UserDetails userDetails = myUserDetailsService.loadUserByUsername(userId);
        final String jwtToken = jwtUtil.generateToken(userDetails);
        return jwtToken;
    }

    private Otp findLatestOtp(List<Otp> otpList) {
        long time = 0l;
        Otp otp = null;
        for (Otp data : otpList) {
            if (data.getCreatedAt().getTime() > time) {
                otp = data;
                time = data.getCreatedAt().getTime();
            }
        }
        return otp;
    }

    private Token saveFreshTokenInDatabase(String token, int userId) {
        Token tokenData = new Token();
        tokenData.setToken(token);
        tokenData.setUserId(userId);
        tokenData.setStatus(TokenStatus.ACTIVE.name());
        tokenData.setCreatedAt(setCurrentDateAndTime());
        tokenData.setUpdatedAt(setCurrentDateAndTime());
        return tokenRepository.save(tokenData);
    }

    private ValidateOtpResponse mapValidateOtpResponse(String token, int userId, Date createdAt) {
        ValidateOtpResponse validateOtpResponse = ValidateOtpResponse.builder().userId(userId).token(token).tokenCreatedTime(createdAt)
                .code(HttpStatus.OK.value()).description(HttpStatus.OK.toString()).build();
        return validateOtpResponse;
    }


    public boolean isUserIdEmail(String loginId) {
        return loginId.contains("@");
    }
}
