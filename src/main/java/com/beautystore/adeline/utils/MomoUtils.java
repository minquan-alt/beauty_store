package com.beautystore.adeline.utils;

import java.nio.charset.StandardCharsets;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import com.beautystore.adeline.exception.AppException;
import com.beautystore.adeline.exception.ErrorCode;

import org.springframework.stereotype.Component;

@Component
public class MomoUtils {
    
    public String createSignature(String rawData, String secretKey) {
        try {
            // Debug với System.out
            System.out.println("=== DEBUG SIGNATURE CREATION ===");
            System.out.println("Raw data: " + rawData);
            System.out.println("Raw data is null: " + (rawData == null));
            System.out.println("Secret key is null: " + (secretKey == null));
            System.out.println("Secret key length: " + (secretKey != null ? secretKey.length() : "null"));
            
            // Validate inputs
            if (rawData == null || rawData.trim().isEmpty()) {
                System.out.println("ERROR: Raw data is null or empty");
                throw new AppException(ErrorCode.ERROR_IN_CREATE_SIGNATURE);
            }
            
            if (secretKey == null || secretKey.trim().isEmpty()) {
                System.out.println("ERROR: Secret key is null or empty");
                throw new AppException(ErrorCode.ERROR_IN_CREATE_SIGNATURE);
            }
            
            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secret_key = new SecretKeySpec(
                secretKey.getBytes(StandardCharsets.UTF_8), 
                "HmacSHA256"
            );
            sha256_HMAC.init(secret_key);
            
            byte[] hash = sha256_HMAC.doFinal(rawData.getBytes(StandardCharsets.UTF_8));
            
            // Convert byte array to hex string
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            
            String signature = hexString.toString();
            System.out.println("Generated signature: " + signature);
            System.out.println("=== END DEBUG ===");
            
            return signature;
            
        } catch (Exception e) {
            System.out.println("ERROR in createSignature: " + e.getMessage());
            e.printStackTrace();
            throw new AppException(ErrorCode.ERROR_IN_CREATE_SIGNATURE);
        }
    }
    
    public boolean verifySignature(String signature, String rawData, String secretKey) {
        try {
            if (signature == null) {
                System.out.println("WARNING: Signature is null");
                return false;
            }
            
            String expectedSignature = createSignature(rawData, secretKey);
            boolean isValid = signature.equals(expectedSignature);
            
            if (!isValid) {
                System.out.println("Signature verification failed");
                System.out.println("Expected: " + expectedSignature);
                System.out.println("Received: " + signature);
            }
            
            return isValid;
            
        } catch (Exception e) {
            System.out.println("ERROR in verifySignature: " + e.getMessage());
            return false;
        }
    }
    
    public String generateRequestId() {
        return String.valueOf(System.currentTimeMillis());
    }
    
    public String generateOrderId() {
        return "ORDER_" + System.currentTimeMillis();
    }
}