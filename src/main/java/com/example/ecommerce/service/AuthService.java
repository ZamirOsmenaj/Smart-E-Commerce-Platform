package com.example.ecommerce.service;

import com.example.ecommerce.domain.User;
import com.example.ecommerce.dto.AuthResponse;
import com.example.ecommerce.dto.LoginRequest;
import com.example.ecommerce.dto.RegisterRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Service responsible for handling authentication operations such as
 * user registration and login.
 */
@Service
@RequiredArgsConstructor
public class AuthService {
    private final JwtService jwtService;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    /**
     * Registers a new user and generates a JWT token for them.
     *
     * @param request the registration request containing user credentials
     *
     * @return an {@link AuthResponse} containing the JWT token for the newly registered user
     */
    public AuthResponse register(RegisterRequest request) {
        User user = userService.registerUser(request);
        String token = jwtService.generateToken(String.valueOf(user.getId()));
        return new AuthResponse(token);
    }

    /**
     * Authenticates an existing user using their email and password,
     * and generates a JWT token if credentials are valid.
     *
     * @param request the login request containing user credentials
     *
     * @return an {@link AuthResponse} containing the JWT token for the authenticated user
     *
     * @throws RuntimeException if the user does not exist or credentials are invalid
     */
    public AuthResponse login(LoginRequest request) {
        User user = userService.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found!"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new RuntimeException("Invalid credentials");
        }

        String token = jwtService.generateToken(String.valueOf(user.getId()));
        return new AuthResponse(token);
    }

}
