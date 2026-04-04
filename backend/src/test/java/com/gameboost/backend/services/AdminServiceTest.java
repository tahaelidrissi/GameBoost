package com.gameboost.backend.services;

import com.gameboost.backend.models.*;
import com.gameboost.backend.repositories.*;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private CoachProfileRepository coachProfileRepository;

    @Mock
    private SessionRepository sessionRepository;

    @InjectMocks
    private AdminService adminService;

    private User playerUser;
    private User coachUserPending;
    private User coachUserApproved;
    private CoachProfile pendingProfile;
    private CoachProfile approvedProfile;
    private Session session;

    @BeforeEach
    void setUp() {
        playerUser = new User();
        playerUser.setId(UUID.randomUUID());
        playerUser.setRole(User.Role.JOUEUR);

        coachUserPending = new User();
        coachUserPending.setId(UUID.randomUUID());
        coachUserPending.setRole(User.Role.COACH);
        coachUserPending.setIsApproved(false);

        coachUserApproved = new User();
        coachUserApproved.setId(UUID.randomUUID());
        coachUserApproved.setRole(User.Role.COACH);
        coachUserApproved.setIsApproved(true);

        pendingProfile = new CoachProfile();
        pendingProfile.setId(10L);
        pendingProfile.setUser(coachUserPending);

        approvedProfile = new CoachProfile();
        approvedProfile.setId(11L);
        approvedProfile.setUser(coachUserApproved);

        session = new Session();
        session.setId(100L);
        session.setStatus(Session.Status.PAID);
        session.setAmount(BigDecimal.valueOf(50.0));
    }

    @Test
    void getPendingCoaches_ShouldReturnOnlyPending() {
        when(coachProfileRepository.findAll()).thenReturn(Arrays.asList(pendingProfile, approvedProfile));

        List<CoachProfile> result = adminService.getPendingCoaches();

        assertEquals(1, result.size());
        assertEquals(pendingProfile.getId(), result.get(0).getId());
        verify(coachProfileRepository, times(1)).findAll();
    }

    @Test
    void approveCoach_WhenFound_ShouldApprove() {
        when(coachProfileRepository.findById(10L)).thenReturn(Optional.of(pendingProfile));
        when(userRepository.save(any(User.class))).thenReturn(coachUserPending);

        User result = adminService.approveCoach(10L);

        assertTrue(result.getIsApproved());
        verify(coachProfileRepository, times(1)).findById(10L);
        verify(userRepository, times(1)).save(coachUserPending);
    }

    @Test
    void approveCoach_WhenNotFound_ShouldThrowException() {
        when(coachProfileRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> adminService.approveCoach(99L));
        verify(coachProfileRepository, times(1)).findById(99L);
        verify(userRepository, never()).save(any());
    }

    @Test
    void rejectCoach_WhenFound_ShouldDelete() {
        when(coachProfileRepository.findById(10L)).thenReturn(Optional.of(pendingProfile));

        adminService.rejectCoach(10L);

        verify(coachProfileRepository, times(1)).findById(10L);
        verify(coachProfileRepository, times(1)).delete(pendingProfile);
        verify(userRepository, times(1)).delete(coachUserPending);
    }

    @Test
    void rejectCoach_WhenNotFound_ShouldThrowException() {
        when(coachProfileRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> adminService.rejectCoach(99L));
        verify(coachProfileRepository, times(1)).findById(99L);
        verify(coachProfileRepository, never()).delete(any());
        verify(userRepository, never()).delete(any());
    }

    @Test
    void getDashboardStats_ShouldReturnCorrectStats() {
        when(userRepository.findAll()).thenReturn(Arrays.asList(playerUser, coachUserPending, coachUserApproved));
        when(coachProfileRepository.findAll()).thenReturn(Arrays.asList(pendingProfile, approvedProfile));
        when(sessionRepository.findAll()).thenReturn(Arrays.asList(session));

        Map<String, Object> stats = adminService.getDashboardStats();

        assertEquals(3, stats.get("total_users"));
        assertEquals(1L, stats.get("total_players"));
        assertEquals(2L, stats.get("total_coaches"));
        assertEquals(1L, stats.get("approved_coaches"));
        assertEquals(1L, stats.get("pending_coaches"));
        assertEquals(1, stats.get("total_sessions"));
        assertEquals(1L, stats.get("completed_sessions"));
        assertEquals(BigDecimal.valueOf(50.0), stats.get("total_revenue"));
    }

    @Test
    void getAllUsers_ShouldReturnAllUsers() {
        when(userRepository.findAll()).thenReturn(Arrays.asList(playerUser, coachUserPending));

        List<User> result = adminService.getAllUsers();

        assertEquals(2, result.size());
        verify(userRepository, times(1)).findAll();
    }
}
