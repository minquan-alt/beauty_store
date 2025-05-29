package com.beautystore.adeline.repository;


import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.beautystore.adeline.entity.Coupon;

@Repository
public interface CouponRepository extends JpaRepository<Coupon, String> {
}