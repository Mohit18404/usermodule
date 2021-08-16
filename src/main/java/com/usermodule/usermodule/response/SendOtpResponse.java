package com.usermodule.usermodule.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class SendOtpResponse {
    private int code;
    private String description;
    private String loginId;
}
