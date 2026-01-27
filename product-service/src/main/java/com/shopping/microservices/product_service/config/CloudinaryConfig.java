package com.shopping.microservices.product_service.config;


import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import lombok.Data;

@Configuration
public class CloudinaryConfig {
    
    @Bean
    public Cloudinary cloudinary(CloudinaryProperties properties) {
        return new Cloudinary(ObjectUtils.asMap(
            "cloud_name", properties.getCloudName(),
            "api_key", properties.getApiKey(),
            "api_secret", properties.getApiSecret(),
            "secure", true
        ));
    }
    
    @Data
    @Configuration
    @ConfigurationProperties(prefix = "cloudinary")
    public static class CloudinaryProperties {
        private String cloudName;
        private String apiKey;
        private String apiSecret;
    }
}