package com.example.wseety.country;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class NumberInfo {
    private boolean status ;
    private String countryCode ;
    private String internationalNumber ;
    private String nationalNumber ;
}
