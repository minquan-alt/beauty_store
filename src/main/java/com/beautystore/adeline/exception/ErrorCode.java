package com.beautystore.adeline.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {
    UNCATEGORIED_EXCEPTION(9999, "Uncategorized Exception"),
    INVALID_KEY(1001, "Invalid key"),
    USER_EXISTED(1002, "User existed"),
    USER_NOT_FOUND(1003, "User not found"),
    USER_NOT_EXISTED(1004, "User not existed"),
    PASSWORD_INVALID(1005, "Password must be at least 8 characters"),
    INVALID_CREDENTIALS(1006, "Password is wrong"),

    COUPON_EXISTED(3001, "Coupon existed"),
    COUPON_NOT_FOUND(3002,"Coupon not found");
    
    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
    private int code;
    private String message;
}
