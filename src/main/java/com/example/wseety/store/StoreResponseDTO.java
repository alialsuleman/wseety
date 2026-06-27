package com.example.wseety.store;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Setter
@Getter
public class StoreResponseDTO {

    private UUID id;

    private String name;

    private String description;

    private String imagePath;

    private long numberOfProducts;

    private long numberOfCompletedOrders;

    private long numberOfBrokers;

    private boolean isAvailable;

    private long rate;

    private CategoryDTO category;

    private StoreUserDTO user;
}
