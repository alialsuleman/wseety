package com.example.wseety.identityVerification.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "verification_documents")
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class VerificationDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private UUID userId;

    @Enumerated(EnumType.STRING)
    private DocumentType documentType;   // NATIONAL_ID_FRONT, NATIONAL_ID_BACK, SELFIE

    private String filePath;             //

    @Enumerated(EnumType.STRING)
    private VerificationStatus status;   // PENDING, APPROVED, REJECTED


    private LocalDateTime uploadedAt;
}