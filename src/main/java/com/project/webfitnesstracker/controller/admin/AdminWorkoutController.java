package com.project.webfitnesstracker.controller.admin;

import com.project.webfitnesstracker.service.WorkoutService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/workouts")
@RequiredArgsConstructor
public class AdminWorkoutController {

    private final WorkoutService workoutService;

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/all")
    public String getAllWorkouts(Model model) {
        model.addAttribute("workouts", workoutService.getAllWorkoutsSortedByDate());
        return "workouts/admins-workout-list";
    }
}
