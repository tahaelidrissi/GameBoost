package com.gameboost.backend.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.*;

@DisplayName("JwtUtils — Tests unitaires")
class JwtUtilsTest {

    private JwtUtils jwtUtils;

    @BeforeEach
    void setUp() {
        jwtUtils = new JwtUtils();
        ReflectionTestUtils.setField(jwtUtils, "jwtSecret", "TestSecretKeyForGameBoostUnitTesting2026!");
        ReflectionTestUtils.setField(jwtUtils, "jwtExpiration", 86400000L);
    }

    @Test
    @DisplayName("Générer un token — doit retourner un token non null")
    void generateToken_shouldReturnNonNullToken() {
        String token = jwtUtils.generateToken("test@mail.com", "JOUEUR", false);
        assertThat(token).isNotNull().isNotBlank();
    }

    @Test
    @DisplayName("Générer un token — doit contenir 3 parties (header.payload.signature)")
    void generateToken_shouldHaveThreeParts() {
        String token = jwtUtils.generateToken("test@mail.com", "COACH", true);
        String[] parts = token.split("\\.");
        assertThat(parts).hasSize(3);
    }

    @Test
    @DisplayName("Extraire l'email — doit retourner l'email du subject")
    void getEmailFromToken_shouldReturnCorrectEmail() {
        String email = "coach@gameboost.com";
        String token = jwtUtils.generateToken(email, "COACH", true);

        String extractedEmail = jwtUtils.getEmailFromToken(token);
        assertThat(extractedEmail).isEqualTo(email);
    }

    @Test
    @DisplayName("Valider un token valide — doit retourner true")
    void validateToken_withValidToken_shouldReturnTrue() {
        String token = jwtUtils.generateToken("test@mail.com", "JOUEUR", false);
        assertThat(jwtUtils.validateToken(token)).isTrue();
    }

    @Test
    @DisplayName("Valider un token invalide — doit retourner false")
    void validateToken_withInvalidToken_shouldReturnFalse() {
        assertThat(jwtUtils.validateToken("invalid.token.here")).isFalse();
    }

    @Test
    @DisplayName("Valider un token null — doit retourner false")
    void validateToken_withNull_shouldReturnFalse() {
        assertThat(jwtUtils.validateToken(null)).isFalse();
    }

    @Test
    @DisplayName("Valider un token vide — doit retourner false")
    void validateToken_withEmpty_shouldReturnFalse() {
        assertThat(jwtUtils.validateToken("")).isFalse();
    }

    @Test
    @DisplayName("Valider un token expiré — doit retourner false")
    void validateToken_withExpiredToken_shouldReturnFalse() {
        // Set expiration to -1 ms (already expired)
        ReflectionTestUtils.setField(jwtUtils, "jwtExpiration", -1000L);
        String token = jwtUtils.generateToken("test@mail.com", "JOUEUR", false);
        assertThat(jwtUtils.validateToken(token)).isFalse();
    }

    @Test
    @DisplayName("Valider un token signé avec une autre clé — doit retourner false")
    void validateToken_withWrongKey_shouldReturnFalse() {
        String token = jwtUtils.generateToken("test@mail.com", "JOUEUR", false);

        // Change the key
        ReflectionTestUtils.setField(jwtUtils, "jwtSecret", "AnotherSecretKeyThatIsDifferent123!");
        assertThat(jwtUtils.validateToken(token)).isFalse();
    }

    @Test
    @DisplayName("Générer des tokens différents pour des emails différents")
    void generateToken_differentEmails_shouldProduceDifferentTokens() {
        String token1 = jwtUtils.generateToken("user1@mail.com", "JOUEUR", false);
        String token2 = jwtUtils.generateToken("user2@mail.com", "COACH", true);
        assertThat(token1).isNotEqualTo(token2);
    }
}
