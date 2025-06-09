package com.beautystore.adeline.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
@Data
public class MomoPaymentRequest {
    private String partnerCode;
    private String partnerName;
    private String storeId;
    private String requestId;
    private Long amount;
    private String orderId;
    private String orderInfo;
    private String redirectUrl;
    private String ipnUrl;
    private String lang;
    private String extraData;
    private String requestType;
    private String signature;
}
