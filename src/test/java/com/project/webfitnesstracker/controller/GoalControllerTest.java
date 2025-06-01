package com.project.webfitnesstracker.controller;

import com.project.webfitnesstracker.dto.request.GoalRequest;
import com.project.webfitnesstracker.dto.response.GoalResponse;
import com.project.webfitnesstracker.model.Goal;
import com.project.webfitnesstracker.model.User;
import com.project.webfitnesstracker.service.GoalService;
import com.project.webfitnesstracker.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GoalControllerTest {

    @Mock
    private GoalService goalService;

    @Mock
    private Model model;

    @Mock
    private BindingResult bindingResult;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private GoalController goalController;

    private GoalRequest goalRequest;
    private Goal goal;
    private User owner;

    @BeforeEach
    void setUp() {
        owner = new User();
        owner.setId(1L);
        owner.setUsername("testuser");

        goalRequest = new GoalRequest();
        goalRequest.setDescription("Lose 5 kg in 2 months");

        goal = new Goal();
        goal.setId(1L);
        goal.setDescription("Lose 5 kg in 2 months");
        goal.setOwner(owner);

        SecurityContextHolder.setContext(securityContext);
        lenient().when(securityContext.getAuthentication()).thenReturn(authentication);
        lenient().when(authentication.getPrincipal()).thenReturn(owner);
    }

    @Test
    void createForm_ShouldReturnCreateFormView() {
        String viewName = goalController.createForm(model, 1L);

        assertEquals("goals/create-goal", viewName);
        verify(model).addAttribute(eq("goal"), any(GoalRequest.class));
        verify(model).addAttribute("ownerId", 1L);
    }

    @Test
    void create_WithValidInput_ShouldRedirectToList() {
        when(bindingResult.hasErrors()).thenReturn(false);

        String viewName = goalController.create(1L, goalRequest, bindingResult, model);

        assertEquals("redirect:/users/1/goals/all", viewName);
        verify(goalService).create(eq(1L), eq(goalRequest));
    }

    @Test
    void create_WithBindingErrors_ShouldReturnCreateForm() {
        when(bindingResult.hasErrors()).thenReturn(true);

        String viewName = goalController.create(1L, goalRequest, bindingResult, model);

        assertEquals("goals/create-goal", viewName);
        verify(model).addAttribute("ownerId", 1L);
        verify(goalService, never()).create(any(), any());
    }

    @Test
    void read_ShouldReturnReadViewWithGoalDetails() {
        when(goalService.readById(1L)).thenReturn(goal);

        String viewName = goalController.read(1L, 1L, model);

        assertEquals("goals/read-goal", viewName);
        verify(model).addAttribute("ownerId", 1L);
        verify(model).addAttribute("goal", goal);
        verify(model).addAttribute("owner", owner);
    }

    @Test
    void updateForm_ShouldReturnUpdateFormView() {
        when(goalService.readById(1L)).thenReturn(goal);
        when(goalService.toRequest(goal)).thenReturn(goalRequest);

        String viewName = goalController.updateForm(1L, 1L, model);

        assertEquals("goals/update-goal", viewName);
        verify(model).addAttribute("goalId", 1L);
        verify(model).addAttribute("goal", goalRequest);
        verify(model).addAttribute("ownerId", 1L);
    }

    @Test
    void update_WithValidInput_ShouldRedirectToReadView() {
        when(bindingResult.hasErrors()).thenReturn(false);
        when(goalService.readById(1L)).thenReturn(goal);

        String viewName = goalController.update(1L, 1L, goalRequest, bindingResult, model);

        assertEquals("redirect:/users/1/goals/1/read", viewName);
        verify(goalService).update(eq(goalRequest), eq(1L), eq(goal));
    }

    @Test
    void update_WithBindingErrors_ShouldReturnUpdateForm() {
        when(bindingResult.hasErrors()).thenReturn(true);

        String viewName = goalController.update(1L, 1L, goalRequest, bindingResult, model);

        assertEquals("goals/update-goal", viewName);
        verify(model).addAttribute("ownerId", 1L);
        verify(model).addAttribute("goalId", 1L);
        verify(model).addAttribute("goal", goalRequest);
        verify(goalService, never()).update(any(), any(), any());
    }

    @Test
    void delete_ShouldRedirectToListView() {
        when(goalService.readById(1L)).thenReturn(goal);

        String viewName = goalController.delete(1L, 1L);

        assertEquals("redirect:/users/1/goals/all", viewName);
        verify(goalService).deleteByEntityThrowing(eq(goal), eq(1L));
    }

    @Test
    void getAllUserGoals_ShouldReturnListView() {
        List<GoalResponse> goals = Collections.singletonList(GoalResponse.builder().build());
        when(goalService.getAllUsersGoals(1L)).thenReturn(goals);

        String viewName = goalController.getAllUserGoals(1L, model);

        assertEquals("goals/goal-list", viewName);
        verify(model).addAttribute("ownerId", 1L);
        verify(model).addAttribute("goals", goals);
    }

    @Test
    void toggleAchieved_ShouldRedirectToListView() {
        when(goalService.readById(1L)).thenReturn(goal);

        String viewName = goalController.toggleAchieved(1L, 1L);

        assertEquals("redirect:/users/1/goals/all", viewName);
        verify(goalService).makeAchieved(eq(goal));
    }

}
