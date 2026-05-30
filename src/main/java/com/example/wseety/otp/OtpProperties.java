package com.example.wseety.otp;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Map;

@Component
@ConfigurationProperties(prefix = "otp")
@Getter
@Setter
public class OtpProperties {

    private int expiryMinutes;
    private int length;
    private Sender sender = new Sender();
    private Retry retry = new Retry();


    // Nested classes
    @Getter
    @Setter
    public static class Sender {
        private String email;
        private String name;
    }

    @Getter
    @Setter
    public static class Retry {
        private int limit;
        private Delay delay = new Delay();
    }

    @Getter
    @Setter
    public static class Delay {
        private int seconds;
    }

}