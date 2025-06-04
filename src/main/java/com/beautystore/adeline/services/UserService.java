package com.beautystore.adeline.services;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.beautystore.adeline.dto.request.UserCreateRequest;
import com.beautystore.adeline.dto.request.UserUpdateRequest;
import com.beautystore.adeline.dto.response.UserResponse;
import com.beautystore.adeline.entity.User;
import com.beautystore.adeline.exception.AppException;
import com.beautystore.adeline.exception.ErrorCode;
import com.beautystore.adeline.mapper.UserMapper;
import com.beautystore.adeline.repository.UserRepository;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private static Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;

    private final UserMapper userMapper;

    public UserResponse createUser(UserCreateRequest request) {
        if (userRepository.existsByEmail(request.getEmail()))
            throw new AppException(ErrorCode.USER_EXISTED);

        User user = userMapper.toUser(request);
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User result = userRepository.save(user);
        return userMapper.toUserResponse(user);
    }

    public List<UserResponse> getUsers() {
        List<User> users = userRepository.findAll();
        if (users.isEmpty()) {
            throw new AppException(ErrorCode.USER_LIST_EMPTY);
        }
        return users
            .stream()
            .map(user -> userMapper.toUserResponse(user))
            .collect(Collectors.toList());
    }

    public int countUsers() {
        return (int) userRepository.count();
    }

    // public UserResponse getUser(Long id) {
    // return userMapper.toUserResponse(userRepository.findById(id)
    // .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND))) ;
    // }
    public UserResponse getUser(Long id) {
        logger.info("Fetching user with id: {}", id); 

        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("User not found with id: {}", id); // 👈 Log lỗi nếu không tìm thấy user
                    return new AppException(ErrorCode.USER_NOT_FOUND);
                });

        logger.info("User found: {}", user); // 

        UserResponse response = userMapper.toUserResponse(user);
        logger.info("Mapped user to response: {}", response); // 

        return response;
    }

    public UserResponse updateUser(UserUpdateRequest request, Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        userMapper.updateUser(user, request);

        return userMapper.toUserResponse(userRepository.save(user));
    }

    public void deleteUser(Long id) {
        try {
            userRepository.deleteById(id);
        } catch (DataIntegrityViolationException ex) {
            throw new AppException(ErrorCode.USER_HAS_DEPENDENCIES);
        }
    }

    @Transactional(readOnly = true)
    public Long getUserIdFromSession(HttpSession session) throws AppException {
        String userEmail = (String) session.getAttribute("userEmail");
        if (userEmail == null) {
            throw new AppException(ErrorCode.USER_NOT_FOUND);
        }
        return userRepository.findIdByEmail(userEmail)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
    }

}