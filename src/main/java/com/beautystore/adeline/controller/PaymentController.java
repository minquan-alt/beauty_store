package com.beautystore.adeline.controller;

import java.util.List;

import com.beautystore.adeline.dto.request.AddPaymentRequest;
import com.beautystore.adeline.dto.response.PaymentResponse;
import com.beautystore.adeline.services.PaymentService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {
    @Autowired
    private PaymentService paymentService;

    @GetMapping("/admin")
    public List<PaymentResponse> getAllPayments() {
        return paymentService.getAllPayments();
    }

    @GetMapping("")
    public List<PaymentResponse> getMyPayments(HttpSession session) {
        return paymentService.getMyPayments(session);
    }

    @GetMapping("/{paymentId}")
    public PaymentResponse getPayment(@PathVariable long paymentId) {
        return paymentService.getPayment(paymentId);
    }

    @PostMapping("")
    public PaymentResponse addPayment(@RequestBody AddPaymentRequest request, HttpSession session) {
        return paymentService.addPayment(request, session);
    }
}
