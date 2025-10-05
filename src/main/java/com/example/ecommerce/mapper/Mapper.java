package com.example.ecommerce.mapper;

import lombok.NonNull;

/**
 * Generic mapper interface defining a contract for converting an entity
 * of type {@code E} to a DTO of type {@code D}.
 *
 * @param <E> the entity type
 * @param <D> the DTO type
 */
public interface Mapper<E, D> {

    /**
     * Converts the given entity into its DTO representation.
     *
     * @param entity the entity to convert (must not be {@code null})
     * @return the DTO corresponding to the given entity
     */
    D toResponseDTO(@NonNull E entity);
}
