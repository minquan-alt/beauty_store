package com.beautystore.adeline.dto.request;

import lombok.Data;

@Data
public class AddAddressRequest {
    String street;
    String city;
    String postalCode;
    String country;
}
