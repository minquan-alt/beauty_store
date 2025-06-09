package com.beautystore.adeline.services;

import java.util.List;
import java.util.stream.Collectors;

import com.beautystore.adeline.dto.request.AddAddressRequest;
import com.beautystore.adeline.dto.request.UpdateAddressRequest;
import com.beautystore.adeline.dto.response.AddressResponse;
import com.beautystore.adeline.entity.Address;
import com.beautystore.adeline.entity.User;
import com.beautystore.adeline.exception.AppException;
import com.beautystore.adeline.exception.ErrorCode;
import com.beautystore.adeline.repository.AddressRepository;
import com.beautystore.adeline.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpSession;

@Service
public class AddressService {

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private UserRepository userRepository;

    public AddressResponse addressToAddressResponse(Address address) {
        return AddressResponse.builder()
            .id(address.getId())
            .city(address.getCity())
            .country(address.getCountry())
            .postalCode(address.getPostalCode())
            .street(address.getStreet())
            .isDefault(address.isDefault())
            .build();
    }
    public List<AddressResponse> getAddresses(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        List<Address> addresses = addressRepository.findByUserId(userId)
            .orElseThrow(() -> new AppException(ErrorCode.ADDRESS_NOT_FOUND));
        
        return addresses
            .stream()
            .map(this::addressToAddressResponse)
            .collect(Collectors.toList());
    }

    public AddressResponse getAddress(Long addressId) {
        Address address = addressRepository.findById(addressId)
            .orElseThrow(() -> new AppException(ErrorCode.ADDRESS_NOT_FOUND));
        return addressToAddressResponse(address);
    }

    public AddressResponse addAddress(HttpSession session, AddAddressRequest request) {
        Long userId = (Long) session.getAttribute("userId");
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        Address address = new Address();
        address.setUser(user);
        address.setCity(request.getCity());
        address.setCountry(request.getCountry());
        address.setPostalCode(request.getPostalCode());
        address.setStreet(request.getStreet());
        addressRepository.save(address);

        return addressToAddressResponse(address);
    }

    public AddressResponse updateAddress(UpdateAddressRequest request) {
        System.out.print("==========REQUEST==========:" + request);
        Address address = addressRepository.findById(request.getId())
            .orElseThrow(() -> new AppException(ErrorCode.ADDRESS_NOT_FOUND));
        
        Boolean isChanged = false;
        
        if(request.getCity() != null && !request.getCity().trim().isEmpty() && !request.getCity().trim().equals(address.getCity())) {
            address.setCity(request.getCity());
            isChanged = true;
        }

        if(request.getCountry() != null && !request.getCountry().trim().isEmpty() && !request.getCountry().trim().equals(address.getCountry())) {
            address.setCountry(request.getCountry());
            isChanged = true;
        }

        if(request.getPostalCode() != null && !request.getPostalCode().trim().isEmpty() && !request.getPostalCode().trim().equals(address.getPostalCode())) {
            address.setPostalCode(request.getPostalCode());
            isChanged = true;
        }

        if(request.getStreet() != null && !request.getStreet().trim().isEmpty() && !request.getStreet().trim().equals(address.getStreet())) {
            address.setStreet(request.getStreet());
            isChanged = true;
        }

        if(isChanged) {
            addressRepository.save(address);
        }
        
        return addressToAddressResponse(address);
    }

    public Boolean updateDefaultAddress(HttpSession session, Long addressId) {
        Long userId = (Long) session.getAttribute("userId");
        Address oldDefaultAddress = addressRepository.findByUserIdAndIsDefault(userId, true)
            .orElseThrow(() -> new AppException(ErrorCode.ADDRESS_NOT_FOUND));

        Address newDefaultAddress = addressRepository.findById(addressId)
            .orElseThrow(() -> new AppException(ErrorCode.ADDRESS_NOT_FOUND));
        
        
        oldDefaultAddress.setDefault(false);
        newDefaultAddress.setDefault(true);

        addressRepository.save(oldDefaultAddress);
        addressRepository.save(newDefaultAddress);

        return true;
    }

}
