package com.beautystore.adeline.dto.request;

import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class UserCreateRequest {
    String name;
    String email;
    @Size(min = 8, message = "PASSWORD_INVALID")
    String password;
    
    @Builder.Default
    String role = "ROLE_USER";
}
