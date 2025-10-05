package com.example.ecommerce.controller;

import com.example.ecommerce.dto.response.ApiResponse;
import com.example.ecommerce.dto.response.AuthResponseDTO;
import com.example.ecommerce.dto.request.LoginRequestDTO;
import com.example.ecommerce.dto.request.RegisterRequestDTO;
import com.example.ecommerce.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller that handles authentication requests such as registration and login.
 */
@RestController
@RequestMapping("api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;

    /**
     * Register a new user in the system and issues a token.
     *
     * @param request the registration details
     * @return a standardized API response containing the authentication result
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponseDTO>> register(@RequestBody RegisterRequestDTO request) {
        try {
            AuthResponseDTO authResponse = authService.register(request);
            
            log.info("AUTH CONTROLLER: User registered successfully with email: {}", request.getEmail());
            return ResponseEntity.ok(ApiResponse.success(authResponse, "User registered successfully"));
        } catch (Exception e) {
            log.error("AUTH CONTROLLER: User registration failed for email {}: {}", request.getEmail(), e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage(), "REGISTRATION_FAILED"));
        }
    }

    /**
     * Authenticates an existing user and issues a token.
     *
     * @param request the login request containing user credentials
     * @return a standardized API response containing the authentication result
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponseDTO>> login(@RequestBody LoginRequestDTO request) {
        try {
            AuthResponseDTO authResponse = authService.login(request);
            
            log.info("AUTH CONTROLLER: User logged in successfully with email: {}", request.getEmail());
            return ResponseEntity.ok(ApiResponse.success(authResponse, "User logged in successfully"));
        } catch (Exception e) {
            log.error("AUTH CONTROLLER: User login failed for email {}: {}", request.getEmail(), e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage(), "LOGIN_FAILED"));
        }
    }
}
