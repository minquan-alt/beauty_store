package com.beautystore.adeline.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.beautystore.adeline.dto.request.ApiResponse;
import com.beautystore.adeline.dto.request.AuthenticationRequest;
import com.beautystore.adeline.dto.response.AuthenticationResponse;
import com.beautystore.adeline.entity.User;
import com.beautystore.adeline.services.AuthenticationService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationController {

    AuthenticationService authenticationService;
    
    @PostMapping("/log-in")
    ApiResponse<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request, HttpServletRequest httpRequest) {
        User user = authenticationService.authenticate(request);
        HttpSession session = httpRequest.getSession(); // Lấy session
        session.setAttribute("userEmail", request.getEmail()); // Lưu email vào session
        boolean isAdmin = "ROLE_ADMIN".equals(user.getRole());
        session.setAttribute("isAdmin", isAdmin);

        return ApiResponse.<AuthenticationResponse>builder()
                .result(AuthenticationResponse.builder()
                    .authenticated(true)
                    .isAdmin(isAdmin)
                    .build())
                .build();
    }

    @GetMapping("/log-out")
    ApiResponse<AuthenticationResponse> logout(HttpServletRequest httpRequest) {
        HttpSession session = httpRequest.getSession(false);
        if(session != null) {
            session.invalidate();
        }
        return ApiResponse.<AuthenticationResponse>builder()
                .result(AuthenticationResponse.builder()
                    .authenticated(false)
                    .redirectTo("/")
                    .build())
                .build();
    }

    @GetMapping("/session-info")
    public String getSessionInfo(HttpSession session) {
        String email = (String) session.getAttribute("userEmail");
        return email != null ? "User email: " + email : "No active session";
    }
}
