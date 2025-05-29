package com.beautystore.adeline.services;

import java.math.BigDecimal;
import java.util.List;

import com.beautystore.adeline.dto.request.AddPaymentRequest;
import com.beautystore.adeline.dto.response.PaymentResponse;
import com.beautystore.adeline.entity.Order;
import com.beautystore.adeline.entity.Payment;
import com.beautystore.adeline.entity.User;
import com.beautystore.adeline.exception.AppException;
import com.beautystore.adeline.exception.ErrorCode;
import com.beautystore.adeline.repository.OrderRepository;
import com.beautystore.adeline.repository.PaymentRepository;
import com.beautystore.adeline.repository.UserRepository;
import com.beautystore.adeline.services.OrderService.TempOrderSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import jakarta.servlet.http.HttpSession;

@Service
public class PaymentService {

    @Autowired
    private UserService userService;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderRepository orderRepository;

    private PaymentResponse mapToPaymentResponse(Payment payment) {
        if (payment == null) return null;

        return PaymentResponse.builder()
                .id(payment.getId())
                .transactionId(payment.getTransactionId())
                .paymentMethod(payment.getPaymentMethod())
                .amount(payment.getAmount())
                .orderId(payment.getOrder() != null ? payment.getOrder().getId() : null)
                .userId(payment.getUser() != null ? payment.getUser().getId() : null)
                .createdAt(payment.getCreatedAt())
                .build();
    }



    public List<PaymentResponse> getAllPayments() {
        List<Payment> results = paymentRepository.findAll();
        if(results.isEmpty()) {
            throw new AppException(ErrorCode.PAYMENT_NOT_EXIST);
        }
        return results
                .stream()
                .map(this::mapToPaymentResponse)
                .toList();
    }

    public List<PaymentResponse> getMyPayments(HttpSession session) {
        Long userId = userService.getUserIdFromSession(session);
        List<Payment> results = paymentRepository.findByUserId(userId);
        if(results.isEmpty()) {
            throw new AppException(ErrorCode.PAYMENT_NOT_FOUND);
        }
        return results
                .stream()
                .map(this::mapToPaymentResponse)
                .toList();
    }

    public PaymentResponse getPayment(long paymentId) {
        Payment result = paymentRepository.findById(paymentId)
                    .orElseThrow(() -> new AppException(ErrorCode.PAYMENT_NOT_EXIST));
        return mapToPaymentResponse(result);
    }

    public PaymentResponse addPayment(@RequestBody AddPaymentRequest request, HttpSession session) {
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

        if(orderId == null) {
            throw new AppException(ErrorCode.ORDER_NOT_FOUND);
        }

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        

        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));

        BigDecimal amount = order.getTotalAmount();

        Payment payment = Payment.builder()
            .order(order)
            .user(user)
            .amount(amount)
            .transactionId(request.getTransactionId())
            .paymentMethod(request.getPaymentMethod())
            .build();

        try {
            Payment savedPayment = paymentRepository.save(payment);
            return PaymentResponse.builder()
            .id(savedPayment.getId())
            .transactionId(savedPayment.getTransactionId())
            .paymentMethod(savedPayment.getPaymentMethod())
            .amount(savedPayment.getAmount())
            .orderId(savedPayment.getOrder().getId())
            .userId(savedPayment.getUser().getId())
            .createdAt(savedPayment.getCreatedAt())
            .build();
        } catch (DataAccessException e) {
            System.out.printf("Database error when saving payment: {}", e.getMessage());
            throw new AppException(ErrorCode.PAYMENT_PROCESS_ERROR);
        }
    }
    // public Payment updatePayment(long paymentId) {

    // }
}
