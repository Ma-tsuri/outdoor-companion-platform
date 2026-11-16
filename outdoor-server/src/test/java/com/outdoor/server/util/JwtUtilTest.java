package com.outdoor.server.util;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class JwtUtilTest {

    @Test
    void shouldGenerateAndParseToken() {
        JwtUtil jwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(jwtUtil, "secret",
                "outdoor-companion-secret-key-2026-keep-longer-than-32");
        ReflectionTestUtils.setField(jwtUtil, "expiration", 86400000L);

        String token = jwtUtil.generateToken(42L, "user_ming", 0);

        assertEquals(42L, jwtUtil.getUserId(token));
        assertEquals("user_ming", jwtUtil.getUsername(token));
        assertEquals(0, jwtUtil.getRole(token));
        assertNotNull(jwtUtil.parseToken(token).getExpiration());
    }
}
