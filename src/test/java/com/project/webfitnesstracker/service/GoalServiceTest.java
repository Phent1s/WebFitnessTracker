package com.project.webfitnesstracker.service;

import com.project.webfitnesstracker.dto.request.GoalRequest;
import com.project.webfitnesstracker.dto.response.GoalResponse;
import com.project.webfitnesstracker.model.Goal;
import com.project.webfitnesstracker.model.User;
import com.project.webfitnesstracker.repository.GoalRepository;
import com.project.webfitnesstracker.security.exception.NullEntityReferenceException;
import com.project.webfitnesstracker.service.mapper.GoalMapper;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GoalServiceTest {

    @Mock
    private GoalRepository goalRepository;

    @Mock
    private UserService userService;

    @Mock
    private GoalMapper goalMapper;

    @InjectMocks
    private GoalService goalService;

    private GoalRequest goalRequest;
    private Goal goal;
    private User owner;

    @BeforeEach
    void setUp(){
        owner = new User();
        owner.setId(1L);
        owner.setUsername("username");

        goalRequest = new GoalRequest();
        goal = new Goal();
        goal.setId(1L);
        goal.setOwner(owner);
    }

    @Test
    void create_shouldSaveGoal(){
        when(userService.findById(1L)).thenReturn(owner);
        when(goalMapper.toEntity(goalRequest, owner)).thenReturn(goal);
        when(goalRepository.save(goal)).thenReturn(goal);

        Goal result = goalService.create(1L, goalRequest);

        assertNotNull(result);
        assertEquals(goal, result);
    }

    @Test
    void create_shouldThrowIfRequestIsNull(){
        assertThrows(NullEntityReferenceException.class, () -> goalService.create(1L, null));
    }

    @Test
    void update_shouldUpdateGoal() {
        when(userService.findById(1L)).thenReturn(owner);
        when(goalMapper.toExistingEntity(goalRequest, owner, goal)).thenReturn(goal);

        goalService.update(goalRequest, 1L, goal);

        verify(goalRepository).save(goal);
    }

    @Test
    void update_shouldThrowAccessDeniedIfOwnerMismatch(){
        User otherUser = new User();
        otherUser.setId(2L);
        goal.setOwner(otherUser);

        assertThrows(AccessDeniedException.class,
                () -> goalService.update(goalRequest, 1L, goal));

    }

    @Test
    void delete_shouldDeleteGoalIfOwnerMatches(){
        goal.setOwner(owner);

        goalService.deleteByEntityThrowing(goal, 1L);

        verify(goalRepository).delete(goal);
    }

    @Test
    void delete_shouldThrowAccessDeniedIfOwnerMismatch(){
        User otherUser = new User();
        otherUser.setId(2L);
        goal.setOwner(otherUser);

        assertThrows(AccessDeniedException.class,
                () -> goalService.deleteByEntityThrowing(goal, 1L));
    }

    @Test
    void getAllUsersGoals_shouldReturnMappedResponses(){
        GoalResponse response = GoalResponse.builder().build();

        when(goalRepository.findByOwner_IdOrderByStartDateAsc(1L)).thenReturn(List.of(goal));
        when(goalMapper.fromEntity(goal)).thenReturn(response);

        List<GoalResponse> result = goalService.getAllUsersGoals(1L);

        assertEquals(1, result.size());
        assertEquals(response, result.get(0));
    }

    @Test
    void getAllGoalsSortedByDate_shouldReturnGoals(){
        when(goalRepository.findAllByOrderByStartDateAsc()).thenReturn(List.of(goal));

        List<Goal> result = goalService.getAllGoalsSortedByDate();

        assertEquals(1, result.size());
        assertEquals(goal, result.get(0));
    }

    @Test
    void makeAchieved_togglesAndSaves(){
        goal.setAchieved(false);

        goalService.makeAchieved(goal);

        assertTrue(goal.isAchieved());
        verify(goalRepository).save(goal);
    }

    @Test
    void makeAchieved_nullGoal_throwsException(){
        assertThrows(NullEntityReferenceException.class,
                () -> goalService.makeAchieved(null));
    }

    @Test
    void readById_shouldReturnGoal(){
        when(goalRepository.findById(1L)).thenReturn(Optional.of(goal));

        Goal result = goalService.readById(1L);

        assertEquals(goal, result);
    }

    @Test
    void readById_shouldThrowIfNotFound(){
        when(goalRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> goalService.readById(1L));
    }

    @Test
    void toRequest_shouldReturnMappedRequest(){
        when(goalMapper.toRequest(goal)).thenReturn(goalRequest);

        GoalRequest result = goalService.toRequest(goal);

        assertEquals(goalRequest, result);
    }
}
