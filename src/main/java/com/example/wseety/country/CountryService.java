package com.example.wseety.country;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.neovisionaries.i18n.CountryCode;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;

@Service
public class CountryService {

    private final PhoneNumberUtil phoneUtil =
            PhoneNumberUtil.getInstance();

    private List<CountryDto> cachedCountries;

    @PostConstruct
    public void loadCountries() {

        cachedCountries = phoneUtil.getSupportedRegions()
                .stream()
                .map(this::createCountry)
                .sorted(Comparator.comparing(CountryDto::name))
                .toList();
    }







    public NumberInfo getNumberInfo(String rawNumber) {

        try {
            Phonenumber.PhoneNumber number =
                    phoneUtil.parse(rawNumber, null);
            // null لأن الرقم فيه country code (+49 ...)

            boolean isValid = phoneUtil.isValidNumber(number);

            String countryCode = phoneUtil.getRegionCodeForNumber(number);

            String international = phoneUtil.format(
                    number,
                    PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL
            );

            String national = phoneUtil.format(
                    number,
                    PhoneNumberUtil.PhoneNumberFormat.NATIONAL
            );

            return NumberInfo.builder()
                    .status(true)
                    .countryCode(countryCode)
                    .internationalNumber(international)
                    .nationalNumber(national)
                    .build();
        } catch (NumberParseException e) {
            return NumberInfo.builder()
                    .status(false)
                    .build();
        }
    }










    public List<CountryDto> getCountries(Locale locale) {

        return cachedCountries.stream()
                .map(country -> new CountryDto(
                        country.isoCode(),
                        new Locale("", country.isoCode())
                                .getDisplayCountry(locale),
                        country.dialCode(),
                        country.flag()
                ))
                .toList();
    }

    private CountryDto createCountry(String isoCode) {

        Locale locale = new Locale("", isoCode);

        return new CountryDto(
                isoCode,
                locale.getDisplayCountry(Locale.ENGLISH),
                "+" + phoneUtil.getCountryCodeForRegion(isoCode),
                flagEmoji(isoCode)
         );
    }







    private String flagEmoji(String countryCode) {

        int first =
                Character.codePointAt(countryCode, 0)
                        - 'A' + 0x1F1E6;

        int second =
                Character.codePointAt(countryCode, 1)
                        - 'A' + 0x1F1E6;

        return new String(Character.toChars(first))
                + new String(Character.toChars(second));
    }
}