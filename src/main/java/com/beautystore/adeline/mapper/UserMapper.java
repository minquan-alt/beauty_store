package com.beautystore.adeline.mapper;

import org.mapstruct.Mapper;

import com.beautystore.adeline.dto.request.UserCreateRequest;
import com.beautystore.adeline.entity.User;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toUser(UserCreateRequest request);
}
