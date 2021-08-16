package com.usermodule.usermodule.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class LoginResponse {
    private int code;
    private String description;
    private Date tokenCreatedTime;
    private String token;
    private int userId;
}
