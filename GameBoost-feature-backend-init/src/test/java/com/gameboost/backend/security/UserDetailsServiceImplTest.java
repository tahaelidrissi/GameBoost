package com.gameboost.backend.security;

import com.gameboost.backend.models.User;
import com.gameboost.backend.repositories.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserDetailsServiceImpl — Tests unitaires")
class UserDetailsServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    private User createUser(User.Role role) {
        return User.builder()
                .id(UUID.randomUUID())
                .email("test@gameboost.com")
                .password("$2a$10$hashedpassword")
                .username("testuser")
                .role(role)
                .isApproved(true)
                .build();
    }

    @Test
    @DisplayName("Charger un utilisateur existant — doit retourner UserDetails avec bon rôle")
    void loadUserByUsername_existingUser_shouldReturnUserDetails() {
        User user = createUser(User.Role.JOUEUR);
        when(userRepository.findByEmail("test@gameboost.com")).thenReturn(Optional.of(user));

        UserDetails userDetails = userDetailsService.loadUserByUsername("test@gameboost.com");

        assertThat(userDetails.getUsername()).isEqualTo("test@gameboost.com");
        assertThat(userDetails.getPassword()).isEqualTo("$2a$10$hashedpassword");
        assertThat(userDetails.getAuthorities()).hasSize(1);
        assertThat(userDetails.getAuthorities().iterator().next().getAuthority())
                .isEqualTo("ROLE_JOUEUR");
    }

    @Test
    @DisplayName("Charger un coach — doit avoir le rôle ROLE_COACH")
    void loadUserByUsername_coach_shouldHaveCoachRole() {
        User user = createUser(User.Role.COACH);
        when(userRepository.findByEmail("test@gameboost.com")).thenReturn(Optional.of(user));

        UserDetails userDetails = userDetailsService.loadUserByUsername("test@gameboost.com");

        assertThat(userDetails.getAuthorities().iterator().next().getAuthority())
                .isEqualTo("ROLE_COACH");
    }

    @Test
    @DisplayName("Charger un admin — doit avoir le rôle ROLE_ADMIN")
    void loadUserByUsername_admin_shouldHaveAdminRole() {
        User user = createUser(User.Role.ADMIN);
        when(userRepository.findByEmail("test@gameboost.com")).thenReturn(Optional.of(user));

        UserDetails userDetails = userDetailsService.loadUserByUsername("test@gameboost.com");

        assertThat(userDetails.getAuthorities().iterator().next().getAuthority())
                .isEqualTo("ROLE_ADMIN");
    }

    @Test
    @DisplayName("Charger un utilisateur inexistant — doit lancer UsernameNotFoundException")
    void loadUserByUsername_nonExistingUser_shouldThrowException() {
        when(userRepository.findByEmail("unknown@mail.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userDetailsService.loadUserByUsername("unknown@mail.com"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("unknown@mail.com");
    }
}
