package com.project.webfitnesstracker.controller;

import com.project.webfitnesstracker.dto.request.WorkoutRequest;
import com.project.webfitnesstracker.model.Workout;
import com.project.webfitnesstracker.service.WorkoutService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/users/{ownerId}/workouts")
@RequiredArgsConstructor
public class WorkoutController {

    private final WorkoutService workoutService;

    @PreAuthorize("hasAuthority('ADMIN') " +
                  "or @userService.isCurrentUser(#ownerId)")
    @GetMapping("/create")
    public String createForm(Model model,
                             @PathVariable Long ownerId) {
        model.addAttribute("workout", new WorkoutRequest());
        model.addAttribute("ownerId", ownerId);
        return "workouts/create-workout";
    }

    @PreAuthorize("hasAuthority('ADMIN') " +
                  "or @userService.isCurrentUser(#ownerId)")
    @PostMapping("/create")
    public String create(@PathVariable Long ownerId,
                         @Validated @ModelAttribute("workout") WorkoutRequest workoutRequest,
                         BindingResult result,
                         Model model) {
        if (result.hasErrors()) {
            model.addAttribute("ownerId", ownerId);
            return "workouts/create-workout";
        }

        workoutService.create(ownerId, workoutRequest);
        return "redirect:/users/" + ownerId + "/workouts/all";
    }

    @PreAuthorize("hasAuthority('ADMIN') " +
                  "or @userService.isCurrentUser(#ownerId)")
    @GetMapping("/{workoutId}/read")
    public String read(@PathVariable Long ownerId,
                       @PathVariable Long workoutId,
                       Model model) {
        Workout workout = workoutService.readById(workoutId);
        model.addAttribute("ownerId", ownerId);
        model.addAttribute("workout", workout);
        model.addAttribute("owner", workout.getOwner());
        return "workouts/read-workout";
    }

    @PreAuthorize("hasAuthority('ADMIN') " +
                  "or @userService.isCurrentUser(#ownerId)")
    @GetMapping("/{workoutId}/update")
    public String updateForm(@PathVariable Long ownerId,
                             @PathVariable Long workoutId,
                             Model model) {
        Workout workout = workoutService.readById(workoutId);
        model.addAttribute("workoutId", workoutId);
        model.addAttribute("workout", workoutService.toRequest(workout));
        model.addAttribute("ownerId", ownerId);
        return "workouts/update-workout";
    }

    @PreAuthorize("hasAuthority('ADMIN') " +
                  "or @userService.isCurrentUser(#ownerId)")
    @PostMapping("/{workoutId}/update")
    public String update(@PathVariable Long ownerId,
                         @PathVariable Long workoutId,
                         @Validated @ModelAttribute("workout") WorkoutRequest workoutRequest,
                         BindingResult result,
                         Model model) {
        if (result.hasErrors()) {
            model.addAttribute("workoutId", workoutId);
            model.addAttribute("ownerId", ownerId);
            model.addAttribute("workout", workoutRequest);
            return "workouts/update-workout";
        }

        workoutService.update(workoutRequest, ownerId, workoutService.readById(workoutId));
        return "redirect:/users/" + ownerId + "/workouts/" + workoutId + "/read";
    }

    @PreAuthorize("hasAuthority('ADMIN') " +
                  "or @userService.isCurrentUser(#ownerId)")
    @PostMapping("/{workoutId}/delete")
    public String delete(@PathVariable Long ownerId,
                         @PathVariable Long workoutId) {
        Workout workout = workoutService.readById(workoutId);
        workoutService.deleteByEntityThrowing(workout, ownerId);
        return "redirect:/users/" + ownerId + "/workouts/all";
    }

    @PreAuthorize("hasAuthority('ADMIN') " +
                  "or @userService.isCurrentUser(#ownerId)")
    @GetMapping("/all")
    public String getAllUserWorkouts(@PathVariable Long ownerId,
                                     Model model) {
        model.addAttribute("ownerId", ownerId);
        model.addAttribute("workouts", workoutService.getAllUsersWorkouts(ownerId));
        return "workouts/workout-list";
    }

}
