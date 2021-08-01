package com.usermodule.usermodule.requests;

import lombok.Data;

@Data
public class LogoutRequest {
    private String loginId;
    private String token;
}
