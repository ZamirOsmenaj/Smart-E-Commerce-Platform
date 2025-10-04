package com.example.ecommerce.controller;

import com.example.ecommerce.dto.response.AuthResponseDTO;
import com.example.ecommerce.dto.request.LoginRequestDTO;
import com.example.ecommerce.dto.request.RegisterRequestDTO;
import com.example.ecommerce.service.AuthService;
import lombok.RequiredArgsConstructor;
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
public class AuthController {

    private final AuthService authService;

    /**
     * Register a new user in the system and issues a token.
     *
     * @param request the registration details
     *
     * @return an {@link AuthResponseDTO}
     */
    @PostMapping("/register")
    public AuthResponseDTO register(@RequestBody RegisterRequestDTO request) {
        return authService.register(request);
    }

    /**
     * Authenticates an existing user and issues a token.
     *
     * @param request the login request containing user credentials
     *
     * @return an {@link AuthResponseDTO}
     */
    @PostMapping("/login")
    public AuthResponseDTO login(@RequestBody LoginRequestDTO request) {
        return authService.login(request);
    }
}
