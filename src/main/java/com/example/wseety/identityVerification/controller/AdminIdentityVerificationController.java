package com.example.wseety.identityVerification.controller;


import com.example.wseety.ApiResponse;
import com.example.wseety.file.FileStorageService;
import com.example.wseety.identityVerification.service.IdentityVerificationService;
import com.example.wseety.identityVerification.entity.VerificationDocument;
import com.example.wseety.identityVerification.entity.VerificationDocumentRepository;
import com.example.wseety.identityVerification.entity.VerificationStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/verification")
@RequiredArgsConstructor
public class AdminIdentityVerificationController {
        // لازم حط صلاحيات لهاد الرابط -
    private final IdentityVerificationService verificationService;
    private final VerificationDocumentRepository documentRepository;
    private final FileStorageService fileStorageService;

    @GetMapping("/admin/pending")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<?>> getPendingDocuments() {
        return ResponseEntity.ok(
                ApiResponse.accepted( "" , documentRepository.findByStatus(VerificationStatus.PENDING))
        );
    }

    @PatchMapping("/admin/review/{documentId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<?>> reviewDocument(
            @PathVariable Long documentId,
            @RequestParam VerificationStatus status  // APPROVED or REJECTED
    ) {
        VerificationDocument doc = documentRepository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Document not found"));
        doc.setStatus(status);
        documentRepository.save(doc);
        return ResponseEntity.ok(ApiResponse.ok("Done !."));
    }
}
