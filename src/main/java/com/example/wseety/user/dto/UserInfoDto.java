package com.example.wseety.user.dto;
import com.example.wseety.user.entity.AcountType;
import com.example.wseety.user.entity.Role;
import com.example.wseety.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoDto {

    private UUID id;
    private String firstname;
    private String lastname;
    private String imagePath;
    private String email;
    private Role role;
    private boolean enabled;
    private String countryCode;
    private String internationalPhoneNumber;
    private AcountType acountType;
    private boolean identityVerified;

    public static UserInfoDto from(User user) {
        return UserInfoDto.builder()
                .id(user.getId())
                .firstname(user.getFirstname())
                .lastname(user.getLastname())
                .imagePath(user.getImagePath())
                .email(user.getEmail())
                .role(user.getRole())
                .enabled(user.isEnabled())
                .countryCode(user.getCountryCode())
                .internationalPhoneNumber(user.getInternationalPhoneNumber())
                .acountType(user.getAcountType())
                .identityVerified(user.isIdentityVerified())
                .build();
    }
}