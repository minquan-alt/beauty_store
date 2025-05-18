package com.beautystore.adeline.repository;

import java.util.List;
import java.util.Optional;

import com.beautystore.adeline.entity.Order;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long>{
    List<Order> findByUserId(Long userId);
    Optional<Order> findByIdAndUserId(Long id, long userId);
}
