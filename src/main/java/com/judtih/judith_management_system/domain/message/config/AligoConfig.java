package com.judtih.judith_management_system.domain.message.config;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "aligo.api")
@Getter
public class AligoConfig {
    private String key;

    private String userId;

    private String sender;
}
