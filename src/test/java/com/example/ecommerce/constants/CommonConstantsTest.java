package com.example.ecommerce.constants;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Simple unit tests for CommonConstants utility class.
 * Testing constant values and utility class behavior.
 */
class CommonConstantsTest {

    @Test
    void shouldHaveCorrectEmptyString() {
        assertEquals("", CommonConstants.EMPTY_STRING);
    }

    @Test
    void shouldHaveCorrectAuthHeader() {
        assertEquals("Authorization", CommonConstants.AUTH_HEADER);
    }

    @Test
    void shouldHaveCorrectBearerPrefix() {
        assertEquals("Bearer ", CommonConstants.BEARER_PREFIX);
    }

    @Test
    void shouldHavePrivateConstructor() throws NoSuchMethodException {
        Constructor<CommonConstants> constructor = CommonConstants.class.getDeclaredConstructor();
        
        // Constructor should be private
        assertFalse(constructor.canAccess(null));
        assertEquals(0, constructor.getParameterCount());
    }

    @Test
    void constantsShouldNotBeNull() {
        assertNotNull(CommonConstants.EMPTY_STRING);
        assertNotNull(CommonConstants.AUTH_HEADER);
        assertNotNull(CommonConstants.BEARER_PREFIX);
    }
}