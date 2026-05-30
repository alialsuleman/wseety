package com.example.wseety.otp;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SendOtpResponse {

    private LocalDateTime expiredAt;
    private LocalDateTime nextResendAt;


}