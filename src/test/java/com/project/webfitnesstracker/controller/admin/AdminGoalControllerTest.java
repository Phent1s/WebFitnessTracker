package com.project.webfitnesstracker.controller.admin;

import com.project.webfitnesstracker.model.Goal;
import com.project.webfitnesstracker.service.GoalService;
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
public class AdminGoalControllerTest {

    @Mock
    private GoalService goalService;

    @Mock
    private Model model;

    @InjectMocks
    private AdminGoalController adminGoalController;

    @Test
    void getAll_ShouldReturnGoalsListView() {
        List<Goal> goals = Collections.singletonList(new Goal());
        when(goalService.getAllGoalsSortedByDate()).thenReturn(goals);

        String viewName = adminGoalController.getAll(model);

        assertEquals("goals/admins-goal-list", viewName);
        verify(model).addAttribute("goals", goals);
        verify(goalService).getAllGoalsSortedByDate();
    }

}
