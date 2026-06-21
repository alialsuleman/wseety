package com.example.wseety.identityVerification.userVerificationStatus;


import com.example.wseety.identityVerification.verificationDocument.entity.VerificationStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "user_verification_statuses")
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserVerificationStatus {

    public UserVerificationStatus (UUID  userId)
    {
        this.userId = userId ;
    }



    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private UUID userId;

    @Builder.Default
    private VerificationStatus nationalIdFront = VerificationStatus.WAITING ;
    @Builder.Default
    private VerificationStatus nationalIdBack= VerificationStatus.WAITING ;
    @Builder.Default
    private VerificationStatus selfie= VerificationStatus.WAITING ;

    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();



    private boolean readyToReview ;
    private boolean isVerified ;




    @PrePersist
    @PreUpdate
    public void updateReadyToReview() {
        int numOfPending =
                (nationalIdFront == VerificationStatus.PENDING ?1:0 )+
                        (nationalIdBack== VerificationStatus.PENDING ?1:0) +
                        (selfie == VerificationStatus.PENDING ?1:0) ;
        int numOfApproved =
                (nationalIdFront == VerificationStatus.APPROVED ?1:0 )+
                        (nationalIdBack== VerificationStatus.APPROVED ?1:0) +
                        (selfie == VerificationStatus.APPROVED ?1:0) ;

        this.readyToReview = (numOfApproved + numOfPending == 3 && numOfPending!=0 ) ;
        this.isVerified = numOfApproved == 3 ;

    }


}