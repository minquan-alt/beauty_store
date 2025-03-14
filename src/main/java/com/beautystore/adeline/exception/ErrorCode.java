package com.beautystore.adeline.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {
    UNCATEGORIED_EXCEPTION(9999, "Uncategorized Exception"),
    INVALID_KEY(1001, "Invalid key"),
    USER_EXISTED(1002, "User existed"),
    PASSWORD_INVALID(1003, "Password must be at least 8 characters")
    ;
    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
    private int code;
    private String message;
}
