package com.beautystore.adeline.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.beautystore.adeline.models.User;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
}