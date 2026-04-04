package com.gameboost.backend.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("JwtAuthFilter — Tests unitaires")
class JwtAuthFilterTest {

    @Mock private JwtUtils jwtUtils;
    @Mock private UserDetailsServiceImpl userDetailsService;
    @Mock private HttpServletRequest request;
    @Mock private HttpServletResponse response;
    @Mock private FilterChain filterChain;

    @InjectMocks
    private JwtAuthFilter jwtAuthFilter;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
    }

    @Nested
    @DisplayName("Sans header Authorization")
    class NoAuthHeader {

        @Test
        @DisplayName("Pas de header — laisse passer sans authentification")
        void noAuthHeader_shouldPassThrough() throws Exception {
            when(request.getHeader("Authorization")).thenReturn(null);

            jwtAuthFilter.doFilterInternal(request, response, filterChain);

            verify(filterChain).doFilter(request, response);
            assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
            verifyNoInteractions(jwtUtils);
        }

        @Test
        @DisplayName("Header sans 'Bearer ' — laisse passer sans authentification")
        void headerWithoutBearer_shouldPassThrough() throws Exception {
            when(request.getHeader("Authorization")).thenReturn("Basic dXNlcjpwYXNz");

            jwtAuthFilter.doFilterInternal(request, response, filterChain);

            verify(filterChain).doFilter(request, response);
            assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
            verifyNoInteractions(jwtUtils);
        }
    }

    @Nested
    @DisplayName("Avec header Authorization invalide")
    class InvalidToken {

        @Test
        @DisplayName("Token invalide — laisse passer sans authentification")
        void invalidToken_shouldPassThrough() throws Exception {
            when(request.getHeader("Authorization")).thenReturn("Bearer bad-token");
            when(jwtUtils.validateToken("bad-token")).thenReturn(false);

            jwtAuthFilter.doFilterInternal(request, response, filterChain);

            verify(filterChain).doFilter(request, response);
            assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
            verify(jwtUtils).validateToken("bad-token");
            verifyNoInteractions(userDetailsService);
        }
    }

    @Nested
    @DisplayName("Avec token JWT valide")
    class ValidToken {

        @Test
        @DisplayName("Token valide — authentifie l'utilisateur dans le contexte Spring")
        void validToken_shouldSetAuthentication() throws Exception {
            UserDetails userDetails = new User(
                "player@mail.com", "encoded",
                List.of(new SimpleGrantedAuthority("ROLE_JOUEUR"))
            );

            when(request.getHeader("Authorization")).thenReturn("Bearer valid-token");
            when(jwtUtils.validateToken("valid-token")).thenReturn(true);
            when(jwtUtils.getEmailFromToken("valid-token")).thenReturn("player@mail.com");
            when(userDetailsService.loadUserByUsername("player@mail.com")).thenReturn(userDetails);

            jwtAuthFilter.doFilterInternal(request, response, filterChain);

            verify(filterChain).doFilter(request, response);
            assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
            assertThat(SecurityContextHolder.getContext().getAuthentication().getName())
                    .isEqualTo("player@mail.com");
            assertThat(SecurityContextHolder.getContext().getAuthentication().getAuthorities())
                    .anyMatch(a -> a.getAuthority().equals("ROLE_JOUEUR"));
        }

        @Test
        @DisplayName("Token valide mais contexte déjà authentifié — ne ré-authentifie pas")
        void validToken_contextAlreadyAuthenticated_shouldNotReAuthenticate() throws Exception {
            // Pré-remplir le contexte de sécurité
            UserDetails userDetails = new User(
                "player@mail.com", "encoded",
                List.of(new SimpleGrantedAuthority("ROLE_JOUEUR"))
            );
            when(request.getHeader("Authorization")).thenReturn("Bearer valid-token");
            when(jwtUtils.validateToken("valid-token")).thenReturn(true);
            when(jwtUtils.getEmailFromToken("valid-token")).thenReturn("player@mail.com");
            when(userDetailsService.loadUserByUsername("player@mail.com")).thenReturn(userDetails);

            // Premier appel — authentifie
            jwtAuthFilter.doFilterInternal(request, response, filterChain);
            assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();

            // Deuxième appel — ne doit pas appeler loadUserByUsername de nouveau
            jwtAuthFilter.doFilterInternal(request, response, filterChain);
            // loadUserByUsername appelé une seule fois car le contexte est déjà rempli
            verify(userDetailsService, times(1)).loadUserByUsername("player@mail.com");
        }
    }
}
