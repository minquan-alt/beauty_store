package com.beautystore.adeline.repository;

import java.util.List;
import java.util.Optional;

import com.beautystore.adeline.entity.Address;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepository extends JpaRepository<Address, Long>{
    Optional<List<Address>> findByUserId(Long id);
    Optional<Address> findByUserIdAndIsDefault(Long id, Boolean isDefault);
}
