package com.example.wseety.identityVerification.userVerificationStatus;

import org.hibernate.query.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

 import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserVerificationStatusRepository extends JpaRepository<UserVerificationStatus, Long> {

    Optional<UserVerificationStatus> findByUserId(UUID userId);
    List<UserVerificationStatus> findByReadyToReviewTrueOrderByTimestampAsc(Pageable pageable);

}
