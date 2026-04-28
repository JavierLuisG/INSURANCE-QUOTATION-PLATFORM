package com.plataformas_danos_back.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
@ConfigurationProperties(prefix = "cache")
@Data
public class CacheProperties {

    private Map<String, Long> ttl = new HashMap<>();
    private Refresh refresh = new Refresh();

    @Data
    public static class Refresh {
        private String cron = "0 0 */6 * * *";
    }
}
