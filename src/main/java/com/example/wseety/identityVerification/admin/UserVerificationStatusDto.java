package com.example.wseety.identityVerification.admin;


import com.example.wseety.identityVerification.userVerificationStatus.UserVerificationStatus;
import com.example.wseety.identityVerification.verificationDocument.entity.VerificationStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;


@Getter
public class UserVerificationStatusDto {


    private final UUID id ;
    private final String firstname;
    private final String lastname;

    private final String email;

    private final VerificationStatus nationalIdFront;
    private final VerificationStatus nationalIdBack;
    private final VerificationStatus selfie;

    private final boolean readyToReview;
    private final boolean verified;
    private final LocalDateTime timestamp;

    public UserVerificationStatusDto(UserVerificationStatus entity, String firstname , String lastname, String email) {
        this.id =  entity.getUserId();
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;

        this.nationalIdFront = entity.getNationalIdFront();
        this.nationalIdBack = entity.getNationalIdBack();
        this.selfie = entity.getSelfie();

        this.readyToReview = entity.isReadyToReview();
        this.verified = entity.isVerified();
        this.timestamp = entity.getTimestamp();
    }


}