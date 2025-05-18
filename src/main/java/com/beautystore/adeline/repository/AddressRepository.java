package com.beautystore.adeline.repository;

import com.beautystore.adeline.entity.Address;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepository extends JpaRepository<Address, Long>{
    
}
