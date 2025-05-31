package com.project.webfitnesstracker.controller;

import com.project.webfitnesstracker.dto.request.WorkoutRequest;
import com.project.webfitnesstracker.dto.response.WorkoutResponse;
import com.project.webfitnesstracker.model.User;
import com.project.webfitnesstracker.model.Workout;
import com.project.webfitnesstracker.service.UserService;
import com.project.webfitnesstracker.service.WorkoutService;
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
public class WorkoutControllerTest {
    @Mock
    private WorkoutService workoutService;

    @Mock
    private UserService userService;

    @Mock
    private Model model;

    @Mock
    private BindingResult bindingResult;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private WorkoutController workoutController;

    private WorkoutRequest workoutRequest;
    private Workout workout;
    private User owner;

    @BeforeEach
    void setUp() {
        owner = new User();
        owner.setId(1L);
        owner.setUsername("testuser");

        workoutRequest = new WorkoutRequest();
        workoutRequest.setType("Morning Workout");


        workout = new Workout();
        workout.setId(1L);
        workout.setType("Morning Workout");
        workout.setOwner(owner);

        SecurityContextHolder.setContext(securityContext);
        lenient().when(securityContext.getAuthentication()).thenReturn(authentication);
        lenient().when(authentication.getPrincipal()).thenReturn(owner);
    }

    @Test
    void createForm_ShouldReturnCreateFormView() {
        String viewName = workoutController.createForm(model, 1L);

        assertEquals("workouts/create-workout", viewName);
        verify(model).addAttribute(eq("workout"), any(WorkoutRequest.class));
        verify(model).addAttribute("ownerId", 1L);
    }

    @Test
    void create_WithValidInput_ShouldRedirectToList() {
        when(bindingResult.hasErrors()).thenReturn(false);

        String viewName = workoutController.create(1L, workoutRequest, bindingResult, model);

        assertEquals("redirect:/users/1/workouts/all", viewName);
        verify(workoutService).create(eq(1L), eq(workoutRequest));
    }

    @Test
    void create_WithBindingErrors_ShouldReturnCreateForm() {
        when(bindingResult.hasErrors()).thenReturn(true);

        String viewName = workoutController.create(1L, workoutRequest, bindingResult, model);

        assertEquals("workouts/create-workout", viewName);
        verify(model).addAttribute("ownerId", 1L);
        verify(workoutService, never()).create(any(), any());
    }

    @Test
    void read_ShouldReturnReadViewWithWorkoutDetails() {
        when(workoutService.readById(1L)).thenReturn(workout);

        String viewName = workoutController.read(1L, 1L, model);

        assertEquals("workouts/read-workout", viewName);
        verify(model).addAttribute("ownerId", 1L);
        verify(model).addAttribute("workout", workout);
        verify(model).addAttribute("owner", owner);
    }

    @Test
    void updateForm_ShouldReturnUpdateFormView() {
        when(workoutService.readById(1L)).thenReturn(workout);
        when(workoutService.toRequest(workout)).thenReturn(workoutRequest);

        String viewName = workoutController.updateForm(1L, 1L, model);

        assertEquals("workouts/update-workout", viewName);
        verify(model).addAttribute("workoutId", 1L);
        verify(model).addAttribute("workout", workoutRequest);
        verify(model).addAttribute("ownerId", 1L);
    }

    @Test
    void update_WithValidInput_ShouldRedirectToReadView() {
        when(bindingResult.hasErrors()).thenReturn(false);
        when(workoutService.readById(1L)).thenReturn(workout);

        String viewName = workoutController.update(1L, 1L, workoutRequest, bindingResult, model);

        assertEquals("redirect:/users/1/workouts/1/read", viewName);
        verify(workoutService).update(eq(workoutRequest), eq(1L), eq(workout));
    }

    @Test
    void update_WithBindingErrors_ShouldReturnUpdateForm() {
        when(bindingResult.hasErrors()).thenReturn(true);

        String viewName = workoutController.update(1L, 1L, workoutRequest, bindingResult, model);

        assertEquals("workouts/update-workout", viewName);
        verify(model).addAttribute("workoutId", 1L);
        verify(model).addAttribute("ownerId", 1L);
        verify(model).addAttribute("workout", workoutRequest);
        verify(workoutService, never()).update(any(), any(), any());
    }

    @Test
    void delete_ShouldRedirectToListView() {
        when(workoutService.readById(1L)).thenReturn(workout);

        String viewName = workoutController.delete(1L, 1L);

        assertEquals("redirect:/users/1/workouts/all", viewName);
        verify(workoutService).deleteByEntityThrowing(eq(workout), eq(1L));
    }

    @Test
    void getAllUserWorkouts_ShouldReturnListView() {
        List<WorkoutResponse> workouts = Collections.singletonList(WorkoutResponse.builder().build());
        when(workoutService.getAllUsersWorkouts(1L)).thenReturn(workouts);

        String viewName = workoutController.getAllUserWorkouts(1L, model);

        assertEquals("workouts/workout-list", viewName);
        verify(model).addAttribute("ownerId", 1L);
        verify(model).addAttribute("workouts", workouts);
    }
}
