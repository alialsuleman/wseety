package com.example.wseety.user.dto;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserSearchDTO {

    private UUID id;
    private String firstname;
    private String lastname;
    private String imagePath;
    private String bio;

    private String facebookLink;
    private String linkedinLink;
    private String telegramLink;
    private String whatsappLink;
}