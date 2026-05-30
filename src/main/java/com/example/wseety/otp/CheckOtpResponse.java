package com.example.wseety.otp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;


@Data
@Builder
@AllArgsConstructor
public class CheckOtpResponse {

    private boolean isVerified ;
    private int numberOfAllowedAttempts;
    private int sendCount;
    private LocalDateTime expiresAt;
    public CheckOtpResponse ()
    {
        this.isVerified = false ;
        this.numberOfAllowedAttempts =0 ;
        this.sendCount =0 ;
        this.expiresAt = LocalDateTime.now( ) ;
    }
    public CheckOtpResponse (boolean isVerified)
    {
        this.isVerified = isVerified ;
        this.numberOfAllowedAttempts =0 ;
        this.sendCount =0 ;
        this.expiresAt = LocalDateTime.now( ) ;
    }
}
