package com.example.wseety.user ;

import com.example.wseety.ApiResponse;
import com.example.wseety.user.dto.ChangePasswordRequest;
import com.example.wseety.user.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/users")
@AllArgsConstructor
public class UserController {



    final private UserService userService ;

    @PatchMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<ApiResponse<?>> changePassword(
            @Valid @RequestBody ChangePasswordRequest changePasswordRequest,
            @AuthenticationPrincipal UserDetails userDetails  ,
           HttpServletRequest servletRequest
    ) {

        String header = servletRequest.getHeader("Authorization");
        String jwt = header.substring(7);
        User user = ((User) userDetails);
        this.userService.changePassword( changePasswordRequest , user , jwt) ;

        return ResponseEntity.ok(ApiResponse.ok("your password has changed !  , please login again", null )) ;
    }



}
