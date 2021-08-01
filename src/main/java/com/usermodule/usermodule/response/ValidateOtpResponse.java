package com.usermodule.usermodule.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ValidateOtpResponse {
    private int code;
    private String description;
    private String tokenCreatedTime;
    private String tokenExpiry;
    private String token;
    private String userId;
}
