package com.usermodule.usermodule.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SendOtpResponse extends BaseResponse {
    private int code;
    private String description;
    private int otp;
}
