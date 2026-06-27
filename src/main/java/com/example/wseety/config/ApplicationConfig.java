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
                new Category("ELECTRONICS", "الكترونيات", "Electronics"),
                new Category("GAMES_SUBSCRIPTIONS", "العاب اشتراكات", "Games & Subscriptions"),
                new Category("SOFTWARE", "برامج", "Software"),
                new Category("CLOTHING", "ملابس", "Clothing"),
                new Category("PHONES", "هواتف", "Phones"),
                new Category("OTHER", "اخرى", "Other")
        ));
      }
    };
  }



}
