package com.beautystore.adeline.repository;

import java.util.List;

import com.beautystore.adeline.entity.Payment;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByUserId(Long userId);
}
