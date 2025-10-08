package com.example.ecommerce.service;

import com.example.ecommerce.constants.MessageConstants;
import com.example.ecommerce.domain.User;
import com.example.ecommerce.dto.response.AuthResponseDTO;
import com.example.ecommerce.dto.request.LoginRequestDTO;
import com.example.ecommerce.dto.request.RegisterRequestDTO;
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
     * @return an {@link AuthResponseDTO} containing the JWT token for the newly registered user
     */
    public AuthResponseDTO register(RegisterRequestDTO request) {
        User user = userService.registerUser(request);

        // Generate token immediately - user is logged in after registration
        String token = jwtService.generateToken(String.valueOf(user.getId()));
        return new AuthResponseDTO(token);
    }

    /**
     * Authenticates an existing user using their email and password,
     * and generates a JWT token if credentials are valid.
     *
     * @param request the login request containing user credentials
     * @return an {@link AuthResponseDTO} containing the JWT token for the authenticated user
     * @throws RuntimeException if the user does not exist or credentials are invalid
     */
    public AuthResponseDTO login(LoginRequestDTO request) {
        User user = userService.findByEmail(request.getEmail());

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new RuntimeException(MessageConstants.INVALID_CREDENTIALS);
        }

        String token = jwtService.generateToken(String.valueOf(user.getId()));
        return new AuthResponseDTO(token);
    }
}
