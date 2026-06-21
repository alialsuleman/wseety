package com.example.wseety.identityVerification.userVerificationStatus;


import com.example.wseety.identityVerification.admin.UserVerificationStatusDto;
import com.example.wseety.identityVerification.verificationDocument.entity.DocumentType;
import com.example.wseety.identityVerification.verificationDocument.entity.VerificationDocument;
import com.example.wseety.identityVerification.verificationDocument.entity.VerificationDocumentRepository;
import com.example.wseety.identityVerification.verificationDocument.entity.VerificationStatus;
import com.example.wseety.user.UserRepository;
import com.example.wseety.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

 import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserVerficationStatusService {

    final private UserVerificationStatusRepository userVerificationStatusRepository ;
    final private VerificationDocumentRepository verificationDocumentRepository ;
    final private UserRepository userRepository ;


    public void updateStatus(long documentId  , VerificationStatus verificationStatus) {

        VerificationDocument doc = verificationDocumentRepository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Document not found"));

        doc.setStatus(verificationStatus);


        UserVerificationStatus userVerificationStatus =
                this.userVerificationStatusRepository.findByUserId(doc.getUserId())
                        .orElse(new UserVerificationStatus(doc.getUserId()));


        switch (doc.getDocumentType()) {
            case NATIONAL_ID_FRONT:
                userVerificationStatus.setNationalIdFront(verificationStatus);
                break;
            case NATIONAL_ID_BACK:
                userVerificationStatus.setNationalIdBack(verificationStatus);
                break;
            case SELFIE:
                userVerificationStatus.setSelfie(verificationStatus);
                break;
            default:
                throw new IllegalArgumentException("Unknown document type: " + doc.getDocumentType());
        }

        this.userVerificationStatusRepository.save(userVerificationStatus);
    }


    public      boolean getUserDocStatus (UUID userId)
    {
        Optional<UserVerificationStatus> userVerificationStatus =
                this.userVerificationStatusRepository.findByUserId(userId) ;
        if (userVerificationStatus.isPresent()) return userVerificationStatus.get().isVerified() ;

        this.userVerificationStatusRepository.save(new UserVerificationStatus(userId)) ;
        return false ;

    }

    public List<UserVerificationStatusDto> getReadyToReview(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);


        List<UserVerificationStatus> statuses =
                userVerificationStatusRepository
                        .findByReadyToReviewTrueOrderByTimestampAsc(pageable);

        return statuses.stream()
                .map(status -> {

                    User user = userRepository.findById(status.getUserId())
                            .orElseThrow();

                    return new UserVerificationStatusDto(
                            status,
                            user.getFirstname(),
                            user.getLastname(),
                            user.getEmail()
                    );
                })
                .toList();

    }

}
