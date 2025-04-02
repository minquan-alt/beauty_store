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

    CART_NOT_FOUND(2001, "Cart not found"),
    CART_ITEM_NOT_FOUND(2002, "Cart item not found"),
    PRODUCT_NOT_FOUND(2003, "Product not found"),
    QUANTITY_REQUIRED(2005, "Quantity is required"),
    QUANTITY_INVALID(2006, "Quantity must be greater than or equal to 0"),
    PRODUCT_ID_REQUIRED(2007, "Product ID is required");

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    private int code;
    private String message;
}
