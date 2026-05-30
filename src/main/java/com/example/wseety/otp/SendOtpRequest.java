package com.example.wseety.otp;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class SendOtpRequest {
    @NotBlank
    private String email;
    @NotNull
    private OtpType type;

}