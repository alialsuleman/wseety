package com.example.wseety.otp;

import com.example.wseety.user.entity.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Otp {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Email(message = "Email must be valid")
    @NotBlank(message = "Email required")
    private String email ;

    private String otp;

    private int attempts;

    private int sendCount;

    private LocalDateTime expiresAt;

    private LocalDateTime lastSentAt;

    private LocalDateTime bannedUntil;

    @Enumerated(EnumType.STRING)
    private OtpType type;
}