package com.example.ecommerce.factory;

import com.example.ecommerce.domain.User;

import java.time.Instant;

/**
 * Factory responsible for creating {@link User} instances.
 */
public class UserFactory {

    public static User createNewUser(String email, String passwordHash) {
        return User.builder()
                .email(email)
                .passwordHash(passwordHash)
                .createdAt(Instant.now())
                .build();
    }
}
