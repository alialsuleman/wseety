package com.example.wseety.user.dto ;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

// ManagerResponse.java
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ManagerResponse {
    private UUID id;
    private String firstname;
    private String lastname;
    private String email;
    private String imagePath;
    private String role;
    private boolean enabled;
}