package com.example.wseety.store;

import com.example.wseety.category.Category;
import com.example.wseety.category.CategoryRepository;
import com.example.wseety.category.CategoryService;
import com.example.wseety.exceptionHandler.exception.BadRequestException;
import com.example.wseety.exceptionHandler.exception.ForbiddenException;
import com.example.wseety.exceptionHandler.exception.NotFoundException;
import com.example.wseety.file.FileStorageService;
import com.example.wseety.identityVerification.userVerificationStatus.UserVerficationStatusService;
import com.example.wseety.identityVerification.userVerificationStatus.UserVerificationStatus;
import com.example.wseety.identityVerification.verificationDocument.entity.VerificationDocument;
import com.example.wseety.identityVerification.verificationDocument.entity.VerificationStatus;
import com.example.wseety.user.entity.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Service
@Builder
@RequiredArgsConstructor
public class StoreService {

    private final UserVerficationStatusService userVerficationStatusService ;
    private final StoreRepository storeRepository;

    private final FileStorageService fileStorageService ;

    private final CategoryService categoryService ;

    public Store createSotre (User user , CreateStoreDto createStoreDto)
    {
        boolean isVerviad =  this.userVerficationStatusService.getUserDocStatus(user.getId()) ;
        Category category =  this.categoryService.findById(createStoreDto.getCategoryId());

        if (!isVerviad) throw new ForbiddenException("User identity not verified. Please complete identity verification before creating a store.");
        Store store =  new Store(user , createStoreDto.getName() , createStoreDto.getDescription(), category) ;
        this.storeRepository.save(store) ;

        category.addStore(store);
        this.categoryService.save(category) ;
        return store  ;
    }

    public Store getMyStore  (User user)
    {
        return
                this.storeRepository.findByUserId(user.getId()).orElseThrow(
                        ()-> new NotFoundException("Store is not found")
                ) ;
    }


    public void updateStoreImage (UUID userId, MultipartFile file)
    {
        Store store = this.storeRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException("Cannot update store image: No store exists for this user."));

        this.fileStorageService.deleteFile(store.getImagePath());


        fileStorageService.validateFileType(
                file,
                List.of("image/jpeg", "image/png", "image/webp")
        );


        String subFolder = "store/profile/" ;
        String storedPath = fileStorageService.uploadFile(file, subFolder);

        store.setImagePath(storedPath);
        this.storeRepository.save(store) ;

    }

    public void deleteStoreImage (UUID userId) {
        Store store = this.storeRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException("Cannot update store image: No store exists for this user."));

        this.fileStorageService.deleteFile(store.getImagePath());
        store.setImagePath(null);
        this.storeRepository.save(store) ;
    }



}
