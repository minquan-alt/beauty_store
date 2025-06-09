package com.beautystore.adeline.dto.request;

import lombok.Data;

@Data
public class UpdateAddressRequest {
    Long id;
    String street;
    String city;
    String postalCode;
    String country;
}
