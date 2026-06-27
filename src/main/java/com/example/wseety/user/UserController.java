package com.example.wseety.user ;

import com.example.wseety.ApiResponse;
import com.example.wseety.user.dto.AddPhoneNumberRequest;
import com.example.wseety.user.dto.ChangePasswordRequest;
import com.example.wseety.user.entity.User;
import com.example.wseety.user.entity.UserInfoDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


@RestController
@RequestMapping("/users")
@AllArgsConstructor
public class UserController {



    final private UserService userService ;


    @PatchMapping("/changepassword")
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

    @PostMapping("/image")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<?>> updateUserImage (
            @RequestParam("file") MultipartFile file ,
            @AuthenticationPrincipal UserDetails userDetails
    )
    {
        User user = ((User) userDetails);
        this.userService.updateUserImage(user , file);
        return ResponseEntity.ok(ApiResponse.ok("The user image has been successfully updated.")) ;
    }


    @GetMapping("/profile")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<?>> getStatus (
            @AuthenticationPrincipal UserDetails userDetails
    )
    {
        User user = ((User) userDetails);
        return ResponseEntity.ok(ApiResponse.ok(UserInfoDto.from(user))) ;

    }


    @DeleteMapping("/image")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<?>> deleteUserImage (
            @AuthenticationPrincipal UserDetails userDetails
    )
    {
        User user = ((User) userDetails);
        this.userService.deleteUserImage(user);
        return ResponseEntity.ok(ApiResponse.ok("The user image has been successfully deleted.")) ;
    }



    @PostMapping("/changephonenumber")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> addPhoneNumber(
            @RequestBody AddPhoneNumberRequest addPhoneNumberRequest ,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        User user = ((User) userDetails);

        this.userService.changePhoneNumber(user ,addPhoneNumberRequest) ;

        return ResponseEntity.ok("");
    }



    @GetMapping("/all")
    public ResponseEntity<ApiResponse<?>> getAllUsers ()
    {
        return ResponseEntity.ok(ApiResponse.ok(this.userService.getAllUsers())) ;

    }

    @PostMapping("/changeRolle")
    public ResponseEntity<ApiResponse<?>> changeRole (
            @RequestParam("userId") UUID userId
    )
    {
        User user =this.userService.changeRolle(userId) ;
        return ResponseEntity.ok(ApiResponse.ok(user)) ;
    }



}
