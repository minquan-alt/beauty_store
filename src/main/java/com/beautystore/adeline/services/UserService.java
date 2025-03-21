package com.beautystore.adeline.services;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.beautystore.adeline.dto.request.UserCreateRequest;
import com.beautystore.adeline.dto.request.UserUpdateRequest;
import com.beautystore.adeline.dto.response.UserResponse;
import com.beautystore.adeline.entity.User;
import com.beautystore.adeline.exception.AppException;
import com.beautystore.adeline.exception.ErrorCode;
import com.beautystore.adeline.mapper.UserMapper;
import com.beautystore.adeline.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private static Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private final UserMapper userMapper;

    public User createUser(UserCreateRequest request) {
        if(userRepository.existsByEmail(request.getEmail()))
            throw new AppException(ErrorCode.USER_EXISTED);

        User user = userMapper.toUser(request);
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public List<User> getUsers() {
        return userRepository.findAll();
    }

    // public UserResponse getUser(Long id) {
    //     return userMapper.toUserResponse(userRepository.findById(id)
    //         .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND))) ;
    // }
    public UserResponse getUser(Long id) {
        logger.info("Fetching user with id: {}", id); // 👈 Log bắt đầu tìm kiếm user

        User user = userRepository.findById(id)
            .orElseThrow(() -> {
                logger.error("User not found with id: {}", id); // 👈 Log lỗi nếu không tìm thấy user
                return new AppException(ErrorCode.USER_NOT_FOUND);
            });

        logger.info("User found: {}", user); // 👈 Log thông tin user đã tìm thấy

        UserResponse response = userMapper.toUserResponse(user);
        logger.info("Mapped user to response: {}", response); // 👈 Log kết quả sau khi map

        return response;
    }

    public UserResponse updateUser(UserUpdateRequest request,Long id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("User not found"));
        userMapper.updateUser(user, request);

        return userMapper.toUserResponse(userRepository.save(user));
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}