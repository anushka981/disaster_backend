package com.anushka.disaster_backend;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JwtServiceTests {
    @Test
    void generatedTokenContainsTheUserAndValidates() {
        JwtService service = new JwtService("test-secret-key-that-is-at-least-thirty-two-bytes", 60_000);
        var user = User.withUsername("responder").password("password").authorities("ROLE_VOLUNTEER").build();
        String token = service.generateToken("responder");

        assertEquals("responder", service.extractUsername(token));
        assertTrue(service.validateToken(token, user));
    }
}
