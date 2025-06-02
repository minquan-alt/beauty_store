package com.beautystore.adeline.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.beautystore.adeline.dto.request.UserCreateRequest;
import com.beautystore.adeline.dto.request.UserUpdateRequest;
import com.beautystore.adeline.dto.response.UserResponse;
import com.beautystore.adeline.entity.User;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toUser(UserCreateRequest request);
    UserResponse toUserResponse(User user);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateUser(@MappingTarget User user, UserUpdateRequest request);
}
