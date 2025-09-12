package com.example.ecommerce.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

/**
 * Entity representing a user of the system.
 */
@Entity
@Table(name = "users")
@Data
public class User {

    /**
     * The unique identifier of the user.
     */
    @Id
    @GeneratedValue
    private UUID id;

    /**
     * The email address of the user.
     * Must be unique and cannot be null.
     */
    @Column(unique = true, nullable = false)
    private String email;

    /**
     * The hashed password of the user.
     * Cannot be null.
     */
    @Column(nullable = false)
    private String passwordHash;

    /**
     * The timestamp when the user account was created.
     */
    private Instant createdAt = Instant.now();

}
