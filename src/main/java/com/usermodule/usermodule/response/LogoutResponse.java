package com.usermodule.usermodule.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LogoutResponse {
    private int code;
    private String description;
    private String token;
}
