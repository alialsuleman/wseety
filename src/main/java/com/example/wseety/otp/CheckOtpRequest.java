package com.example.wseety.otp;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class CheckOtpRequest {
    @NotBlank
    private String email;
    @NotNull
    private OtpType type;
    @NotNull
    private String otpCode ;



}