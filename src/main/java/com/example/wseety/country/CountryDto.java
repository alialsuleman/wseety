package com.example.wseety.country;


public record CountryDto(
        String isoCode,
        String name,
        String dialCode,
        String flag
) {
}