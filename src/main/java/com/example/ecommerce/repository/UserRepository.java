package com.example.ecommerce.repository;

import com.example.ecommerce.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for mapping {@link User} entities.
 * <p>
 * Extends {@link JpaRepository} to provide standard CRUD operations.
 */
public interface UserRepository extends JpaRepository<User, UUID> {

    /**
     * Retrieves a user by their email address.
     *
     * @param email the email to search for, must not be {@code null}
     *
     * @return an {@link Optional} containing the matching {@link User}, if found,
     *         or an empty {@link Optional} if no user exists with the given email.
     */
    Optional<User> findByEmail(String email);
}
