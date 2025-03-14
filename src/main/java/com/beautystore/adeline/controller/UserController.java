package com.beautystore.adeline.controller;

import java.util.List;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.beautystore.adeline.dto.request.UserCreateRequest;
import com.beautystore.adeline.dto.request.UserUpdateRequest;
import com.beautystore.adeline.dto.request.ApiResponse;
import com.beautystore.adeline.entity.User;
import com.beautystore.adeline.services.UserService;

@RestController
@RequestMapping("/users")
public class UserController {

    
    @Autowired
    private UserService userService;

    @PostMapping()
    ApiResponse<User> createUser(@RequestBody @Valid UserCreateRequest request){
        ApiResponse<User> apiResponse = new ApiResponse<>();
        apiResponse.setResult(userService.createUser(request));
        return apiResponse;
    }

    @GetMapping()
    List<User> getUsers() {
        return userService.getUsers();
    }

    @GetMapping("/{userId}")
    User getUser(@PathVariable Long userId) {
        return userService.getUser(userId);
    }


    @PutMapping("/{userId}")
    User updateUser(@RequestBody UserUpdateRequest request,@PathVariable Long userId){
        return userService.updateUser(request, userId);
    }

    @DeleteMapping("/{userId}")
    String deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
        return "User has been deleted";
    }

}
