package com.example.wseety.identityVerification.verificationDocument.controller;

import com.example.wseety.ApiResponse;
import com.example.wseety.file.FileStorageService;
import com.example.wseety.identityVerification.userVerificationStatus.UserVerficationStatusService;
import com.example.wseety.identityVerification.verificationDocument.entity.DocumentType;
import com.example.wseety.identityVerification.verificationDocument.service.IdentityVerificationService;
import com.example.wseety.identityVerification.verificationDocument.entity.VerificationDocument;
import com.example.wseety.identityVerification.verificationDocument.entity.VerificationDocumentRepository;
import com.example.wseety.user.entity.User;
import org.springframework.core.io.Resource ;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/verification")
@RequiredArgsConstructor
public class IdentityVerificationController {

    private final IdentityVerificationService verificationService;
    private final VerificationDocumentRepository documentRepository;
    private final FileStorageService fileStorageService;

    private final UserVerficationStatusService userVerficationStatusService;

    // -------------------------------------------------------
    // UserSection
    // -------------------------------------------------------
    @PostMapping("/upload")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<?>>  uploadDocument(
            @RequestParam MultipartFile file,
            @RequestParam DocumentType documentType,
            @AuthenticationPrincipal UserDetails userDetails
    ) {

        User user = ((User) userDetails);
        VerificationDocument doc = verificationService.uploadIdentityDocument(file, user, documentType);
        return ResponseEntity.ok(ApiResponse.accepted("The file was uploaded successfully.", doc));
    }


    @GetMapping("/document")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<?>> getDocument(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        UUID userId = ((User) userDetails).getId();
        List<VerificationDocument> verificationDocuments =
                this.verificationService.getAllUserDocs(userId);
        return ResponseEntity.ok(ApiResponse.accepted("success.", verificationDocuments));
    }



//    @DeleteMapping("/document/{documentId}")
//    @PreAuthorize("hasRole('USER')")
//    public ResponseEntity<?> deleteDocument(
//            @PathVariable Long documentId,
//            @AuthenticationPrincipal UserDetails userDetails
//    ) {
//
//        User user = ((User) userDetails);
//        this.verificationService.deleteDocument(user , documentId);
//
//        return ResponseEntity.ok(ApiResponse.accepted("The file was successfully deleted." , null ));
//    }


    @GetMapping("/document/{documentId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Resource> getDocument(
            @PathVariable Long documentId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {

        User user = ((User) userDetails);

        Map<String, Object> meta = this.verificationService.getImage(documentId, user);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType((String) meta.get("contentType")))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + meta.get("fileName") + "\"")
                .header(HttpHeaders.CACHE_CONTROL, "no-store, no-cache, must-revalidate")
                .header(HttpHeaders.PRAGMA, "no-cache")
                .body((Resource) meta.get("resource"));
    }


    @GetMapping("/status")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<?>> getStatus(
            @AuthenticationPrincipal UserDetails userDetails
    ) {

        User user = ((User) userDetails);

        Map<String, Boolean > res = Map.of(
              "isVerfied" ,   this.userVerficationStatusService.getUserDocStatus(user.getId())
        ) ;

        return  ResponseEntity.ok(ApiResponse.ok(res)) ;
    }





}