package com.example.wseety.store;


import com.example.wseety.user.entity.AcountType;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Setter
@Getter
public class StoreUserDTO {

    private UUID id;

    private String firstname;
    private String lastname;

    private String imagePath;

    private String email;

    private String countryCode;
    private String internationalPhoneNumber;

    private AcountType acountType;

    private boolean identityVerified;
}