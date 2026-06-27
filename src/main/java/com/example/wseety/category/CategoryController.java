package com.example.wseety.category;


import com.example.wseety.ApiResponse;
import com.example.wseety.store.CreateStoreDto;
import com.example.wseety.user.entity.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("category")
@RequiredArgsConstructor
public class CategoryController {


    final private CategoryService categoryService;

    @GetMapping("/")
    public ResponseEntity<ApiResponse<?>> getCategories()
    {
        return ResponseEntity.ok(ApiResponse.ok(this.categoryService.getall())) ;
    }
}
