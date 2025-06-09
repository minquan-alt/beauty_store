package com.beautystore.adeline.controller;
import com.beautystore.adeline.services.MomoPaymentService;
import com.beautystore.adeline.dto.request.MomoCallbackRequest;
import com.beautystore.adeline.dto.response.ApiResponse;
import com.beautystore.adeline.dto.response.MomoPaymentResponse;
import com.beautystore.adeline.entity.Payment;
import com.beautystore.adeline.exception.AppException;
import com.beautystore.adeline.exception.ErrorCode;
import com.beautystore.adeline.repository.PaymentRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import jakarta.servlet.http.HttpSession;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/momo")
@RequiredArgsConstructor
@Slf4j
public class MomoController {
    
    private final MomoPaymentService momoPaymentService;
    private final PaymentRepository paymentRepository;
    
    @PostMapping("/create")
    public ApiResponse<Map<String, Object>> createPayment(HttpSession session) {
        MomoPaymentResponse response = momoPaymentService.createPayment(session);
        
        if (response != null && response.getResultCode() == 0) {
            Map<String, Object> result = Map.of(
                "success", true,
                "payUrl", response.getPayUrl(),
                "orderId", response.getOrderId(),
                "qrCodeUrl", response.getQrCodeUrl() != null ? response.getQrCodeUrl() : "",
                "message", "Payment created successfully"
            );
            
            ApiResponse<Map<String, Object>> apiResponse = new ApiResponse<>();
            apiResponse.setResult(result);
            return apiResponse;
        } else {
            throw new AppException(ErrorCode.PAYMENT_CREATION_FAILED);
        }
    }
    
    @PostMapping("/callback")
    public ResponseEntity<?> handleCallback(@RequestBody MomoCallbackRequest callback) {
        log.info("Received MoMo callback: {}", callback);
        
        boolean processed = momoPaymentService.processCallback(callback);
        
        if (processed) {
            return ResponseEntity.ok("OK");
        } else {
            return ResponseEntity.badRequest().body("Error processing callback");
        }
    }
    
    @GetMapping("/return")
    public RedirectView handleReturn(
            @RequestParam String orderId,
            @RequestParam Integer resultCode,
            @RequestParam(required = false) String message,
            RedirectAttributes redirectAttributes) {
        
        log.info("Payment return for MoMo order: {}, result: {}", orderId, resultCode);
        
        // Tìm payment để lấy order ID thực
        Optional<Payment> paymentOpt = paymentRepository.findByTransactionId(orderId);
        Payment payment = paymentOpt.get();
        if (paymentOpt.isPresent()) {
            Long realOrderId = payment.getOrder().getId();
            
            if (resultCode == 0) {
                payment.setStatus(Payment.PaymentStatus.Completed);
                paymentRepository.save(payment);
                redirectAttributes.addAttribute("orderId", realOrderId);
                redirectAttributes.addFlashAttribute("message", "Payment successful!");
        
                return new RedirectView("/payment/success");
            } else {
                payment.setStatus(Payment.PaymentStatus.Failed);
                paymentRepository.save(payment);

                redirectAttributes.addAttribute("orderId", realOrderId);
                redirectAttributes.addFlashAttribute("message", "Payment failed!");
        
                return new RedirectView("/payment/failure");
            }
            
        } else {
            return new RedirectView("/payment/failure?message=Order not found");
        }
    }
    
    @GetMapping("/status/{transactionId}")
    public ApiResponse<Map<String, Object>> getPaymentStatus(@PathVariable String transactionId) {
        Optional<Payment> paymentOpt = paymentRepository.findByTransactionId(transactionId);
        
        if (paymentOpt.isPresent()) {
            Payment payment = paymentOpt.get();
            Map<String, Object> result = Map.of(
                "success", true,
                "orderId", payment.getOrder().getId(),
                "amount", payment.getAmount(),
                "status", payment.getStatus().toString(),
                "paymentMethod", payment.getPaymentMethod().toString(),
                "createdAt", payment.getCreatedAt()
            );
            
            ApiResponse<Map<String, Object>> apiResponse = new ApiResponse<>();
            apiResponse.setResult(result);
            return apiResponse;
        } else {
            throw new AppException(ErrorCode.PAYMENT_NOT_FOUND);
        }
    }
}