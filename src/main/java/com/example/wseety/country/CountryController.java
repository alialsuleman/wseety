package com.example.wseety.country;



import com.example.wseety.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Locale;

@RestController
@RequestMapping("/countries")
@RequiredArgsConstructor
public class CountryController {

    private final CountryService countryService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<CountryDto>>> getCountries(
            @RequestHeader(
                    value = "Accept-Language",
                    required = false
            ) Locale locale
    ) {

        if (locale == null) {
            locale = Locale.ENGLISH;
        }

        return ResponseEntity.ok(ApiResponse.ok(countryService.getCountries(locale)));
    }



}