package com.example.wseety.store;


import com.example.wseety.ApiResponse;
import com.example.wseety.user.entity.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("store")
@RequiredArgsConstructor
public class StoreControllar {

    final private StoreService storeService ;


    @PostMapping("/create")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<?>> createStore(
            @RequestBody @Valid  CreateStoreDto createStoreDto ,
            @AuthenticationPrincipal UserDetails userDetails
    )
    {
        User user = ((User) userDetails);
     ;
        return ResponseEntity.ok(ApiResponse.ok(
                "The store has been successfully created."
                , this.storeService.createSotre(user.getId() , createStoreDto)
        )) ;
    }

    @PostMapping("/image")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<?>> updateStoreImage (
            @RequestParam("file") MultipartFile file ,
            @AuthenticationPrincipal UserDetails userDetails
    )
    {
        User user = ((User) userDetails);
        this.storeService.updateStoreImage(user.getId() , file);
        return ResponseEntity.ok(ApiResponse.ok("The store image has been successfully updated.")) ;
    }


    @DeleteMapping("/image")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<?>> deleteStoreImage (
            @AuthenticationPrincipal UserDetails userDetails
    )
    {
        User user = ((User) userDetails);
        this.storeService.deleteStoreImage(user.getId() );
        return ResponseEntity.ok(ApiResponse.ok("The store image has been successfully deleted.")) ;
    }



}
