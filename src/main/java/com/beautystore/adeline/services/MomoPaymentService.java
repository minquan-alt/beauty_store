package com.beautystore.adeline.services;

import java.util.Optional;

import com.beautystore.adeline.config.MomoConfig;
import com.beautystore.adeline.dto.request.MomoCallbackRequest;
import com.beautystore.adeline.dto.request.MomoPaymentRequest;
import com.beautystore.adeline.dto.response.MomoPaymentResponse;
import com.beautystore.adeline.entity.Order;
import com.beautystore.adeline.entity.Payment;
import com.beautystore.adeline.entity.User;
import com.beautystore.adeline.exception.AppException;
import com.beautystore.adeline.exception.ErrorCode;
import com.beautystore.adeline.repository.OrderRepository;
import com.beautystore.adeline.repository.PaymentRepository;
import com.beautystore.adeline.repository.UserRepository;
import com.beautystore.adeline.services.OrderService.TempOrderSession;
import com.beautystore.adeline.utils.MomoUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;


@Service
@RequiredArgsConstructor
@Slf4j
public class MomoPaymentService {
    
    private final MomoConfig momoConfig;
    private final MomoUtils momoUtils;
    private final RestTemplate restTemplate;
    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    
    @Transactional
    public MomoPaymentResponse createPayment(HttpSession session) {
        try {
            Long userId = (Long) session.getAttribute("userId");
            TempOrderSession orderSession = (TempOrderSession) session.getAttribute("orderSession");

            if(userId == null) {
                throw new AppException(ErrorCode.USER_NOT_LOGIN);
            }

            if(orderSession == null) {
                throw new AppException(ErrorCode.ORDER_NOT_IN_SESSION);
            }

            if(orderSession.isExpired()) {
                throw new AppException(ErrorCode.ORDER_IS_EXPIRED);
            }
            Long orderId = orderSession.getOrderId();

            Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));
            
            User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

            System.out.println(order);
            
            // Kiểm tra xem đã có payment cho order này chưa
            Optional<Payment> existingPayment = paymentRepository.findByOrderId(orderId);
            if (existingPayment.isPresent()) {
                throw new AppException(ErrorCode.PAYMENT_EXISTS);
            }
            
            String requestId = momoUtils.generateRequestId();
            String momoOrderId = momoUtils.generateOrderId();
            Long amount = order.getTotalAmount().longValue() * 26000;
            String orderInfo = "Thanh toán đơn hàng #" + orderId;

            System.out.println("=== DEBUG MOMO CONFIG ===");
            System.out.println("Partner Code: " + momoConfig.getPartnerCode());
            System.out.println("Access Key: " + momoConfig.getAccessKey());
            System.out.println("Secret Key is null: " + (momoConfig.getSecretKey() == null));
            System.out.println("Secret Key length: " + (momoConfig.getSecretKey() != null ? momoConfig.getSecretKey().length() : "null"));
            System.out.println("IPN URL: " + momoConfig.getIpnUrl());
            System.out.println("Redirect URL: " + momoConfig.getRedirectUrl());

            System.out.println("=== DEBUG PAYMENT DATA ===");
            System.out.println("Order ID: " + orderId);
            System.out.println("MoMo Order ID: " + momoOrderId);
            System.out.println("Request ID: " + requestId);
            System.out.println("Amount: " + amount);
            System.out.println("Order Info: " + orderInfo);

            
            // Tạo raw signature
            String rawSignature = String.format(
                "accessKey=%s&amount=%d&extraData=%s&ipnUrl=%s&orderId=%s&orderInfo=%s&partnerCode=%s&redirectUrl=%s&requestId=%s&requestType=%s",
                momoConfig.getAccessKey(),
                amount,
                "", // extraData
                momoConfig.getIpnUrl(),
                momoOrderId,
                orderInfo,
                momoConfig.getPartnerCode(),
                momoConfig.getRedirectUrl(),
                requestId,
                "payWithMethod"
            );
            
            String signature = momoUtils.createSignature(rawSignature, momoConfig.getSecretKey());
            
            // Tạo request
            MomoPaymentRequest request = new MomoPaymentRequest();
            request.setPartnerCode(momoConfig.getPartnerCode());
            request.setPartnerName("Beauty Store Adeline");
            request.setStoreId(momoConfig.getPartnerCode());
            request.setRequestId(requestId);
            request.setAmount(amount);
            request.setOrderId(momoOrderId);
            request.setOrderInfo(orderInfo);
            request.setRedirectUrl(momoConfig.getRedirectUrl());
            request.setIpnUrl(momoConfig.getIpnUrl());
            request.setLang("vi");
            request.setExtraData("");
            request.setRequestType("payWithMethod");
            request.setSignature(signature);
            
            // Gửi request đến MoMo
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            HttpEntity<MomoPaymentRequest> entity = new HttpEntity<>(request, headers);
            
            ResponseEntity<MomoPaymentResponse> response = restTemplate.exchange(
                momoConfig.getEndpoint(),
                HttpMethod.POST,
                entity,
                MomoPaymentResponse.class
            );
            
            MomoPaymentResponse momoResponse = response.getBody();
            System.out.println("Momo payment response: " + momoResponse);
            
            if (momoResponse != null && momoResponse.getResultCode() == 0) {
                // Tạo payment record trong database
                Payment payment = Payment.builder()
                    .order(order)
                    .user(user)
                    .amount(order.getTotalAmount())
                    .transactionId(momoOrderId) 
                    .paymentMethod(Payment.PaymentMethod.Momo)
                    .status(Payment.PaymentStatus.Pending)
                    .build();
                
                paymentRepository.save(payment);
                System.out.println("Created payment record with transaction ID: " + momoOrderId);
            }
            
            return momoResponse;
            
        } catch (AppException e) {
            throw e;
        }
    }

    public boolean processCallback(MomoCallbackRequest callback) {
        try {

            System.out.println("=== DEBUG MOMO CALLBACK  ===");
            if (callback == null || callback.getOrderId() == null) {
                System.out.println("callback is null");

                return false;
            }
            
            if (!verifyCallback(callback)) {
                System.out.println("Invalid signature for callback: " + callback);
                return false;
            }
            
            Optional<Payment> paymentOpt = paymentRepository.findByTransactionId(callback.getOrderId());
            
            if (paymentOpt.isEmpty()) {
                log.error("Payment not found for transaction ID: {}", callback.getOrderId());
                return false;
            }
            
            // Payment payment = paymentOpt.get();
            
            // Cập nhật trạng thái payment
            // if (callback.getResultCode() == 0) {
            //     payment.setStatus(Payment.PaymentStatus.Completed);
            //     System.out.println("Payment completed for order: " + payment.getOrder().getId());
            // } else {
            //     payment.setStatus(Payment.PaymentStatus.Failed);
            //     System.out.println("Payment failed for order: " + payment.getOrder().getId() + " with message: " + callback.getMessage());
            // }
            
            // paymentRepository.save(payment);
            // System.out.println("Payment update success with status: " + payment.getStatus().toString());
            return true;
            
        } catch (Exception e) {
            System.out.println("Error processing callback: " + e);
            return false;
        }
    }
    public boolean verifyCallback(MomoCallbackRequest callback) {
        try {
            if (callback == null) {
                return false;
            }
            
            String rawSignature = String.format(
                "accessKey=%s&amount=%d&extraData=%s&message=%s&orderId=%s&orderInfo=%s&orderType=%s&partnerCode=%s&payType=%s&requestId=%s&responseTime=%d&resultCode=%d&transId=%d",
                momoConfig.getAccessKey(),
                callback.getAmount(),
                callback.getExtraData() != null ? callback.getExtraData() : "",
                callback.getMessage() != null ? callback.getMessage() : "",
                callback.getOrderId(),
                callback.getOrderInfo() != null ? callback.getOrderInfo() : "",
                callback.getOrderType() != null ? callback.getOrderType() : "",
                callback.getPartnerCode(),
                callback.getPayType() != null ? callback.getPayType() : "",
                callback.getRequestId(),
                callback.getResponseTime(),
                callback.getResultCode(),
                callback.getTransId()
            );
            
            return momoUtils.verifySignature(callback.getSignature(), rawSignature, momoConfig.getSecretKey());
            
        } catch (Exception e) {
            log.error("Error verifying callback", e);
            return false;
        }
    }
}