package com.example.wseety.identityVerification.verificationDocument.service;

import com.example.wseety.exceptionHandler.exception.BadRequestException;
import com.example.wseety.exceptionHandler.exception.ConflictException;
import com.example.wseety.exceptionHandler.exception.ForbiddenException;
import com.example.wseety.exceptionHandler.exception.UnauthorizedException;
import com.example.wseety.file.FileStorageService;
import com.example.wseety.identityVerification.userVerificationStatus.UserVerficationStatusService;
import com.example.wseety.identityVerification.userVerificationStatus.UserVerificationStatus;
import com.example.wseety.identityVerification.userVerificationStatus.UserVerificationStatusRepository;
import com.example.wseety.identityVerification.verificationDocument.entity.DocumentType;
import com.example.wseety.identityVerification.verificationDocument.entity.VerificationDocument;
import com.example.wseety.identityVerification.verificationDocument.entity.VerificationDocumentRepository;
import com.example.wseety.identityVerification.verificationDocument.entity.VerificationStatus;
import com.example.wseety.user.entity.Role;
import com.example.wseety.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class IdentityVerificationService {

    private final FileStorageService fileStorageService;
    private final VerificationDocumentRepository documentRepository;
    private final UserVerificationStatusRepository userVerificationStatusRepository ;
    private final UserVerficationStatusService userVerficationStatusService ;




    public VerificationDocument uploadIdentityDocument(
            MultipartFile file,
            User user,
            DocumentType documentType  // NATIONAL_ID_FRONT, NATIONAL_ID_BACK, SELFIE
    ) {

        UUID userId =  user.getId() ;
        fileStorageService.validateFileType(
                file,
                List.of("image/jpeg", "image/png", "image/webp")
        );

        Optional<VerificationDocument> verificationDocument =
                this.documentRepository.findByDocumentTypeAndUserId(documentType, userId) ;



        if (verificationDocument.isPresent() &&   verificationDocument.get().getStatus()== VerificationStatus.REJECTED )
        {
            System.out.println("hi");
            deleteDocument(user,verificationDocument.get().getId() );
        }
        else if (verificationDocument.isPresent())
            throw new ForbiddenException("You cannot delete a file while it is under review; please wait for the admin's response.");

        String subFolder = "private/identity/" + userId;
        String storedPath = fileStorageService.uploadFile(file, subFolder);


        VerificationDocument doc = VerificationDocument.builder()
                .userId(userId)
                .documentType(documentType)
                .filePath(storedPath)
                .status(VerificationStatus.PENDING)
                .uploadedAt(LocalDateTime.now())
                .build();

        VerificationDocument savedDocs = documentRepository.save(doc);
        this.userVerficationStatusService.updateStatus(savedDocs.getId(), VerificationStatus.PENDING );

        return savedDocs ;
    }


    public List<VerificationDocument> getAllUserDocs (UUID userId )
    {
        List <VerificationDocument> verificationDocuments =
                this.documentRepository.findByUserId(userId) ;
        return verificationDocuments ;
    }


    public void deleteDocument  (User user , Long documentId )
    {
        boolean isAdmin =  user.getRole().toString() == Role.ADMIN.toString()  ;

        VerificationDocument doc = isAdmin ?
                this.documentRepository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Document not found")):
                this.documentRepository.findByIdAndUserId(documentId, user.getId())
                .orElseThrow(() -> new RuntimeException("Document not found or access denied")) ;

        if ( doc.getStatus() == VerificationStatus.APPROVED ) {
            throw new ConflictException("Cannot delete a reviewed document") ;
        }

        fileStorageService.deleteFile(doc.getFilePath());
        documentRepository.delete(doc);

    }


    public  Map<String, Object> getImage(Long documentId, User user)
    {

        VerificationDocument doc = documentRepository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Document not found"));

        UUID requesterId = user.getId();
        boolean isAdmin =  user.getRole() ==  Role.ADMIN ;

        if (!isAdmin && !doc.getUserId().equals(requesterId)) {
            throw new UnauthorizedException("") ;
        }

        String filePath = doc.getFilePath();
        if (filePath.contains("..")) {
            throw new BadRequestException("Error: The image is invalid for display.") ;
        }

        Map<String, Object> meta = fileStorageService
                .downloadFileWithMeta(filePath);
        return meta ;
    }

}