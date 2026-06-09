package com.example.wseety.store;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;


@Getter
public class CreateStoreDto {
    @NotBlank
    @NotNull
    private String name ;

    String description ;

}
