package com.project.webfitnesstracker.controller.admin;

import com.project.webfitnesstracker.dto.response.WorkoutResponse;
import com.project.webfitnesstracker.model.Workout;
import com.project.webfitnesstracker.service.WorkoutService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AdminWorkoutControllerTest {

    @Mock
    private WorkoutService workoutService;

    @Mock
    private Model model;

    @InjectMocks
    private AdminWorkoutController adminWorkoutController;

    @Test
    void getAllWorkouts_ShouldReturnWorkoutsListView() {
        List<Workout> workouts = Collections.singletonList(new Workout());
        when(workoutService.getAllWorkoutsSortedByDate()).thenReturn(workouts);

        String viewName = adminWorkoutController.getAllWorkouts(model);

        assertEquals("workouts/admins-workout-list", viewName);
        verify(model).addAttribute("workouts", workouts);
        verify(workoutService).getAllWorkoutsSortedByDate();
    }

}
