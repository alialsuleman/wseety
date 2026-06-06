package com.example.wseety.user ;


import com.example.wseety.auth.AuthenticationService;
import com.example.wseety.exceptionHandler.exception.BadRequestException;
import com.example.wseety.exceptionHandler.exception.UnauthorizedException;
import com.example.wseety.google.GoogleUser;
import com.example.wseety.token.Token;
import com.example.wseety.token.TokenRepository;
import com.example.wseety.user.dto.ChangePasswordRequest;
import com.example.wseety.user.entity.Role;
import com.example.wseety.user.entity.User;
import lombok.RequiredArgsConstructor;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
@RequiredArgsConstructor
public class UserService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository repository;
    private final TokenRepository tokenRepository ;
    private final AuthenticationService authenticationService ;

    public void changePassword(ChangePasswordRequest request, User user , String token) {
        boolean  havePermissionToChangePassword =
                checkCurrentPassword (user , request.getCurrentPassword())
                        ||
                checkAuthChangingPassword(token) ;
        if (havePermissionToChangePassword == false  )
            throw new UnauthorizedException("You do not have permission to change the password.") ;

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        repository.save(user);
        this.authenticationService.revokeAllUserTokens(user);

    }


    private boolean checkCurrentPassword (User user, String currentPassword) {
            if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
               return false ;
            }
            return true ;
    }
    private boolean checkAuthChangingPassword (String tokenText )
    {
        Optional<Token> token =  tokenRepository.findByToken(tokenText) ;
        if (token.isPresent() && token.get().isResetPassword() == true )return true  ;
        return false ;
    }


    public User findOrCreateGoogleUser (GoogleUser googleUser)
    {
        var user =  this.repository.findByEmail(googleUser.getEmail())
                .orElseGet(() -> {
                    User newUser = User.builder()
                            .firstname(extractFirstName(googleUser.getName()))
                            .lastname(extractLastName(googleUser.getName()))
                            .email(googleUser.getEmail())
                            .imagePath(googleUser.getPicture())
                            .password(null)
                            .role(Role.USER)
                            .enabled(true)
                            .build();
                    var savedUser = repository.save(newUser);
                    return savedUser;
                });
        if (user.isEnabled() ==  false )
        {
            user.setEnabled(true);
            user = repository.save(user) ;
        }
        return user ;
    }

    private String extractFirstName(String fullName) {
        if (fullName == null || fullName.isEmpty()) return "";
        String[] names = fullName.split(" ");
        return names[0];
    }

    private String extractLastName(String fullName) {
        if (fullName == null || fullName.isEmpty()) return "";
        String[] names = fullName.split(" ");
        return names.length > 1 ? names[names.length - 1] : "";
    }

}
