package com.example.wseety.user ;


import com.example.wseety.auth.AuthenticationService;
import com.example.wseety.country.CountryService;
import com.example.wseety.country.NumberInfo;
import com.example.wseety.exceptionHandler.exception.BadRequestException;
import com.example.wseety.exceptionHandler.exception.NotFoundException;
import com.example.wseety.exceptionHandler.exception.UnauthorizedException;
import com.example.wseety.file.FileStorageService;
import com.example.wseety.google.GoogleUser;
import com.example.wseety.store.Store;
import com.example.wseety.token.Token;
import com.example.wseety.token.TokenRepository;
import com.example.wseety.user.dto.AddPhoneNumberRequest;
import com.example.wseety.user.dto.ChangeAccountTypeRequest;
import com.example.wseety.user.dto.ChangePasswordRequest;
import com.example.wseety.user.entity.AcountType;
import com.example.wseety.user.entity.Role;
import com.example.wseety.user.entity.User;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository repository;
    private final TokenRepository tokenRepository ;
    private final AuthenticationService authenticationService ;

    private final FileStorageService fileStorageService ;

    private final CountryService  countryService ;



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



    public void updateUserImage (User user, MultipartFile file)
    {

        this.fileStorageService.deleteFile(user.getImagePath());


        fileStorageService.validateFileType(
                file,
                List.of("image/jpeg", "image/png", "image/webp")
        );


        String subFolder = "user/profile/" ;
        String storedPath = fileStorageService.uploadFile(file, subFolder);

        user.setImagePath(storedPath);
        this.repository.save(user) ;

    }

    public void deleteUserImage (User user) {

        this.fileStorageService.deleteFile(user.getImagePath());
        user.setImagePath(null);
        this.repository.save(user) ;
    }


    public void changeAccountType (User user, ChangeAccountTypeRequest changeAccountTypeRequest)
    {
        if (changeAccountTypeRequest.getAcountType() ==  AcountType.USER)
            throw new BadRequestException("It is not possible to change the account type right now; please contact support.");
        user.setAcountType(changeAccountTypeRequest.getAcountType());
        repository.save (user);
    }



    // admin section ...
    public List<User> getAllUsers ( )
    {
        return this.repository.findAll() ;
    }

    public User changeRolle (UUID userId)
    {
        User user =  this.repository.findById(userId).get();
        if (user != null)
        {
            user.setRole(Role.ADMIN);
            this.repository.save(user) ;
        }
        return user ;
    }

    ///////



    void changePhoneNumber (User user, AddPhoneNumberRequest addPhoneNumberRequest)
    {
        NumberInfo numberInfo = this.countryService.getNumberInfo(addPhoneNumberRequest.phoneNumber()) ;
        if (numberInfo.isStatus() == false)  throw new BadRequestException("Invalid phone number") ;

        user.setCountryCode(numberInfo.getCountryCode());
        user.setInternationalPhoneNumber(numberInfo.getInternationalNumber());
        this.repository.save(user) ;
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
