package com.plataformas_danos_back.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "validation")
@Data
public class ValidationProperties {

    private int inconsistencyThresholdPercent = 10;
}
