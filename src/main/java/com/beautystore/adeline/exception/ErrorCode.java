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
    USER_LIST_EMPTY(1007, "User list is empty"),
    USER_NOT_LOGIN(1008, "User has not login"),
    USER_HAS_DEPENDENCIES(1009, "User cannot be deleted because they have associated coupon usage"),


    CART_NOT_FOUND(2001, "Cart not found"),
    CART_ITEM_NOT_FOUND(2002, "Cart item not found"),
    PRODUCT_NOT_FOUND(2003, "Product not found"),
    QUANTITY_REQUIRED(2005, "Quantity is required"),
    QUANTITY_INVALID(2006, "Quantity must be greater than or equal to 0"),
    PRODUCT_ID_REQUIRED(2007, "Product ID is required"),
    PRODUCT_LIST_EMPTY(2008, "Product is empty"),
    PRODUCT_REQUIRED(2009, "Product is required"),
    ADD_CART_RESPONSE_NOT_FOUND(2010, "Error in add to cart"),

    SUPPLIER_NOT_FOUND(3001, "Supplier not found"),
   
    CATEGORY_EXISTED(4001, "Category existed"),
    CATEGORY_LIST_EMPTY(4002, "Category is empty"),
    CATEGORY_NOT_FOUND(4003, "Category not found"),
    CATEGORY_HAS_PRODUCTS(4004, "Cannot delete category because it has associated products"),

    COUPON_EXISTED(5001, "Coupon existed"),
    COUPON_NOT_FOUND(5002,"Coupon not found"),
    COUPON_EXPIRED(5003, "Coupon is expired"),
    COUPON_MIN_ORDER_NOT_MET(5004, "Coupon min is not met"),

    ORDER_NOT_EXISTED(6001, "Order not existed"),
    ORDER_NOT_FOUND(6002, "Order not existed"),
    ORDER_NOT_IN_SESSION(6003, "Order has not created"),
    ITEMS_NOT_EXISTED(6004, "Order has no items"),
    UNIT_PRICE_REQUIRED(6005, "Unit price is required"),
    PAYMENT_REQUIRED(6006, "Payment is required"),
    ORDER_IS_EXPIRED(6007, "Order is expired"),
    ORDER_ID_NOT_IN_SESSION(6008, "OrderId not in session"),
    PRODUCT_NOT_FOUND_IN_INVENTORY(6009, "Inventory does not have this product"),
    STOCK_QUANTITY_NOT_ENOUGH(6010, "Stock quantity is not enough"),


    ADDRESS_NOT_NULL(7001, "Address must not be null"),
    ADDRESS_NOT_FOUND(7002, "Address not found"),

    PAYMENT_NOT_FOUND(8001, "There is no payments"),
    PAYMENT_NOT_EXIST(8002, "Payment not exists"),
    PAYMENT_PROCESS_ERROR(8003, "You met error in payment process"),


    ;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    private int code;
    private String message;
}
