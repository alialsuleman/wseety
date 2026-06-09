package com.example.wseety.identityVerification.admin;


import com.example.wseety.identityVerification.userVerificationStatus.UserVerficationStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminIdentityVerificationService {

    private final UserVerficationStatusService userVerficationStatusService ;


    void changeUserStatus  ( )
    {

    }


}
