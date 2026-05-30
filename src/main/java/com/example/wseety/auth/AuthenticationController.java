package com.example.wseety.auth;


import com.example.wseety.ApiResponse;
import com.example.wseety.auth.dto.AuthenticationRequest;
import com.example.wseety.auth.dto.AuthenticationResponse;
import com.example.wseety.auth.dto.RegisterRequest;
import com.example.wseety.user.entity.Role;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDateTime;


@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {

  private final AuthenticationService service;



  @PostMapping("/register")
  public ResponseEntity<ApiResponse<?>> register(
          @Valid @RequestBody RegisterRequest request
  ) {
    AuthenticationResponse authenticationResponse = service.registerAsUser(request) ;

    ApiResponse<?> response = ApiResponse.builder()
            .success(true)
            .message("register successfully! now you have to confirm your email")
            .timestamp(LocalDateTime.now())
            .status(HttpStatus.OK.value())
            .data (authenticationResponse)
            .build();

    return ResponseEntity.ok(response);
  }



  // login
  @PostMapping("/authenticate")
  public ResponseEntity<ApiResponse<AuthenticationResponse>> authenticate(
          @Valid  @RequestBody AuthenticationRequest request
  ) {
        AuthenticationResponse authenticateResponse= service.authenticate(request) ;

        ApiResponse<AuthenticationResponse> response = ApiResponse.<AuthenticationResponse>builder()
              .success(true)
              .message("login successfully")
              .timestamp(LocalDateTime.now())
              .status(HttpStatus.OK.value())
              .data (authenticateResponse)
              .build();

        return ResponseEntity.ok(response);
   }

  @GetMapping("/refresh-token")
  public ResponseEntity<?> refreshToken(
      HttpServletRequest request,
      HttpServletResponse response
  ) throws IOException {

    AuthenticationResponse res=  service.refreshToken(request, response);
    if (res == null)
    {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED.value()).body(
              ApiResponse.<Void>builder()
                      .success(false)
                      .message("login again")
                      .data(null)
                      .status(HttpStatus.UNAUTHORIZED.value())
                      .build());
    }
    else {
      ApiResponse<AuthenticationResponse> response2 = ApiResponse.<AuthenticationResponse>builder()
              .success(true)
              .message("done !")
              .timestamp(LocalDateTime.now())
              .status(HttpStatus.OK.value())
              .data(res)
              .build();

      return ResponseEntity.ok(response2);
    }
  }






}
