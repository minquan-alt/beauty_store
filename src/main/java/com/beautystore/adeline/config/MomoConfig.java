package com.beautystore.adeline.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import lombok.Data;

@Component
@ConfigurationProperties(prefix = "momo")
@Data
public class MomoConfig {
    private String partnerCode;
    private String accessKey;
    private String secretKey;
    private String endpoint;
    private String redirectUrl;
    private String ipnUrl;

    @PostConstruct
    public void init() {
        System.out.println("=== MOMO CONFIG POST CONSTRUCT ===");
        System.out.println("Partner Code loaded: " + (partnerCode != null ? "YES" : "NO"));
        System.out.println("Access Key loaded: " + (accessKey != null ? "YES" : "NO"));
        System.out.println("Secret Key loaded: " + (secretKey != null ? "YES" : "NO"));
        System.out.println("Endpoint: " + endpoint);
        System.out.println("IPN URL: " + ipnUrl);
        System.out.println("Redirect URL: " + redirectUrl);
        
        if (partnerCode == null || accessKey == null || secretKey == null) {
            System.out.println("ERROR: MoMo configuration is not loaded properly!");
            System.out.println("Check your application.properties file!");
        }
    }
}