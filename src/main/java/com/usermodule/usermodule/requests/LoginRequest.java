package com.usermodule.usermodule.requests;

import lombok.Data;

@Data
public class LoginRequest {

    private String loginId;
    private String password;

}
