package com.example.wseety.identityVerification.admin;


import com.example.wseety.ApiResponse;
import com.example.wseety.file.FileStorageService;
import com.example.wseety.identityVerification.userVerificationStatus.UserVerficationStatusService;
import com.example.wseety.identityVerification.userVerificationStatus.UserVerificationStatus;
import com.example.wseety.identityVerification.verificationDocument.service.IdentityVerificationService;
import com.example.wseety.identityVerification.verificationDocument.entity.VerificationDocument;
import com.example.wseety.identityVerification.verificationDocument.entity.VerificationDocumentRepository;
import com.example.wseety.identityVerification.verificationDocument.entity.VerificationStatus;
import com.example.wseety.user.entity.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.websocket.server.PathParam;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

// Fretig
// Testing ??
@RestController
@RequestMapping("/admin/verification")
@RequiredArgsConstructor
@Validated
public class AdminIdentityVerificationController {


    private final UserVerficationStatusService userVerficationStatusService ;
    private final IdentityVerificationService identityVerificationService ;

    @GetMapping("/pending")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<?>> getPendingDocuments(
            @RequestParam("page") int page ,
            @RequestParam("size") int size
            ) {
        return ResponseEntity.ok(
                ApiResponse.accepted( "" , userVerficationStatusService.getReadyToReview(page , size))
        );
    }

    @PatchMapping("/review/{documentId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<?>> reviewDocument(
            @PathVariable Long documentId,
            @RequestParam VerificationStatus status  // APPROVED or REJECTED
    ) {

        userVerficationStatusService.updateStatus(documentId, status);

        return ResponseEntity.ok(ApiResponse.ok("Done !."));
    }





    @GetMapping("/document")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<?>> getDocument(
            @PathParam("userId") UUID userId
    ) {

        List<VerificationDocument> verificationDocuments =
                this.identityVerificationService.getAllUserDocs(userId);
        return ResponseEntity.ok(ApiResponse.accepted("success.", verificationDocuments));
    }



    @GetMapping("/document/{documentId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Resource> getDocument(
            @PathVariable Long documentId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {

        User user = ((User) userDetails);

        Map<String, Object> meta = this.identityVerificationService.getImage(documentId, user);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType((String) meta.get("contentType")))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + meta.get("fileName") + "\"")
                .header(HttpHeaders.CACHE_CONTROL, "no-store, no-cache, must-revalidate")
                .header(HttpHeaders.PRAGMA, "no-cache")
                .body((Resource) meta.get("resource"));
    }






//
//    @GetMapping("/status/{userId}")
//    @PreAuthorize("hasRole('ADMIN')")
//    public ResponseEntity<ApiResponse<?>> getStatus(
//            @PathVariable @NotBlank UUID userId,
//            @AuthenticationPrincipal UserDetails userDetails
//    ) {
//
//        Map<String, Boolean > res = Map.of(
//                "isVerfied" ,   this.userVerficationStatusService.getUserDocStatus(userId)
//        ) ;
//
//        return  ResponseEntity.ok(ApiResponse.ok(res)) ;
//    }

}
