package com.example.wseety.otp;

import com.example.wseety.ApiResponse;
import com.example.wseety.auth.AuthenticationService;
import com.example.wseety.auth.dto.AuthenticationResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.TooManyListenersException;


@RestController
@RequestMapping("/otp")
@AllArgsConstructor
public class OtpController {

    private final OtpService otpService ;
    private final AuthenticationService authenticationService ;

    @PostMapping("/send")
    public ResponseEntity<ApiResponse<?>> sendOtp(@Valid @RequestBody SendOtpRequest request) throws TooManyListenersException {

        SendOtpResponse sendOtpResponse = otpService.sendotp(request) ;
        return ResponseEntity.ok(
                ApiResponse.builder()
                        .success(true)
                        .data(sendOtpResponse)
                        .message("The Otp code has been sent successfully.")
                        .status(HttpStatus.OK.value())
                        .build()
        );

    }

    @PostMapping("/check")
    public ResponseEntity<ApiResponse<?>> checkOtp(@Valid @RequestBody CheckOtpRequest request) throws TooManyListenersException {

        CheckOtpResponse checkOtpResponse = otpService.checkOtp(request) ;
        AuthenticationResponse authenticationResponse = authenticationService.createJwtResponse(request.getEmail()) ;
        return ResponseEntity.ok(
                ApiResponse.builder()
                        .success(true)
                        .data(authenticationResponse)
                        .message("The Otp code has been successfully verified.")
                        .status(HttpStatus.OK.value())
                        .build()
        );

    }



}



