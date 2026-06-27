package com.example.wseety.config;


import com.example.wseety.auditing.ApplicationAuditAware;
import com.example.wseety.category.Category;
import com.example.wseety.category.CategoryRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.web.client.RestTemplate;
import org.springframework.boot.web.client.RestTemplateBuilder;

import java.time.Duration;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

@Configuration
@RequiredArgsConstructor
public class ApplicationConfig {

  @Bean
  public RestTemplate restTemplate(RestTemplateBuilder builder) {
    return builder
            .setConnectTimeout(Duration.ofSeconds(3)) //
            .setReadTimeout(Duration.ofSeconds(5))    //
            .build();
  }


  @Bean
  public AuditorAware<UUID> auditorAware() {
    return new ApplicationAuditAware();
  }

  @Bean
  public ObjectMapper objectMapper() {
    return new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
  }

  @Bean
  CommandLineRunner initCategories(CategoryRepository repo) {
    return args -> {

      if (repo.count() == 0) {
        repo.saveAll(List.of(
                new Category("DIGITAL_CARDS", "بطاقات رقمية", "Digital Cards"),
                new Category("GAMES_CREDIT", "شحن ألعاب والرصيد", "Games & Credit Top-up"),
                new Category("PHONES_ACCESSORIES", "هواتف واكسسوارات", "Phones & Accessories"),
                new Category("COMPUTERS", "حواسيب", "Computers"),
                new Category("WASITI_MARKET", "وسيطي ماركت", "Wasiti Market"),
                new Category("CLOTHING_ACCESSORIES", "ملابس واكسسوارات", "Clothing & Accessories"),
                new Category("GLOBAL_BRANDS", "براندات عالمية", "Global Brands"),
                new Category("LOCAL_BRANDS", "براندات محلية", "Local Brands"),
                new Category("GIFTS", "هدايا", "Gifts"),
                new Category("GLOBAL_STORE", "المتجر العالمي", "Global Store"),
                new Category("RESTAURANTS", "مطاعم", "Restaurants")
        ));
      }
    };
  }



}
