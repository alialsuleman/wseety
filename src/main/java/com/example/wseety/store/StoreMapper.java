package com.example.wseety.store;

public class StoreMapper {

    public static StoreResponseDTO toDto(Store store) {

        StoreResponseDTO dto = new StoreResponseDTO();

        dto.setId(store.getId());
        dto.setName(store.getName());
        dto.setDescription(store.getDescription());
        dto.setImagePath(store.getImagePath());

        dto.setNumberOfProducts(store.getNumberOfProducts());
        dto.setNumberOfCompletedOrders(store.getNumberOfCompletedOrders());
        dto.setNumberOfBrokers(store.getNumberOfBrokers());
        dto.setAvailable(store.isAvailable());
        dto.setRate(store.getRate());

        // Category
        if (store.getCategory() != null) {
            CategoryDTO categoryDTO = new CategoryDTO();
            categoryDTO.setId(store.getCategory().getId());
            categoryDTO.setNameAr(store.getCategory().getNameAr());
            categoryDTO.setNameEn(store.getCategory().getNameEn());
            dto.setCategory(categoryDTO);
        }

        // User
        if (store.getUser() != null) {
            StoreUserDTO userDTO = new StoreUserDTO();

            userDTO.setId(store.getUser().getId());
            userDTO.setFirstname(store.getUser().getFirstname());
            userDTO.setLastname(store.getUser().getLastname());
            userDTO.setImagePath(store.getUser().getImagePath());
            userDTO.setEmail(store.getUser().getEmail());
            userDTO.setCountryCode(store.getUser().getCountryCode());
            userDTO.setInternationalPhoneNumber(store.getUser().getInternationalPhoneNumber());
            userDTO.setAcountType(store.getUser().getAcountType());
            userDTO.setIdentityVerified(store.getUser().isIdentityVerified());

            dto.setUser(userDTO);
        }

        return dto;
    }
}