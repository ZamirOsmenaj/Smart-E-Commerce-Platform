package com.example.ecommerce.service;

import com.example.ecommerce.decorator.EcommerceNotificationService;
import com.example.ecommerce.domain.User;
import com.example.ecommerce.dto.RegisterRequestDTO;
import com.example.ecommerce.factory.UserFactory;
import com.example.ecommerce.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service responsible for managing user data.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EcommerceNotificationService notificationService;

    /**
     * Registers a new user by creating a {@link User} entity,
     * encoding their password, and saving them to the repository.
     * Automatically sends welcome notification using the Decorator pattern.
     *
     * @param request the registration request containing email and password
     *
     * @return the saved {@link User} entity
     */
    @Transactional
    public User registerUser(RegisterRequestDTO request) {
        User user = UserFactory.createNewUser(request.getEmail(), passwordEncoder.encode(request.getPassword()));
        User savedUser = userRepository.save(user);

        // Send welcome notification using Decorator pattern
        log.info("Sending welcome notification to new user: {}\n", savedUser.getEmail());
        notificationService.sendWelcomeNotification(savedUser);

        return savedUser;
    }

    /**
     * Finds a user by their email address.
     *
     * @param email the email of the user to find
     *
     * @return an {@link Optional} containing the {@link User} if found, or empty if not
     */
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}
