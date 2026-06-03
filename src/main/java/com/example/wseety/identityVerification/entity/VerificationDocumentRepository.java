package com.example.wseety.identityVerification.entity;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


public interface VerificationDocumentRepository extends JpaRepository<VerificationDocument, Long> {
    List<VerificationDocument> findByStatus (VerificationStatus verificationStatus) ;
    Optional<VerificationDocument> findByIdAndUserId (Long id , UUID userId) ;
    List<VerificationDocument> findByUserId(UUID userId) ;
    Optional<VerificationDocument> findByDocumentTypeAndUserId(DocumentType documentType ,UUID userId ) ;
}
