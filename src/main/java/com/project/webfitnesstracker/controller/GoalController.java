package com.project.webfitnesstracker.controller;

import com.project.webfitnesstracker.dto.request.GoalRequest;
import com.project.webfitnesstracker.dto.request.WorkoutRequest;
import com.project.webfitnesstracker.model.Goal;
import com.project.webfitnesstracker.service.GoalService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/users/{ownerId}/goals")
@RequiredArgsConstructor
public class GoalController {

    private final GoalService goalService;

    @PreAuthorize("hasAuthority('ADMIN') " +
                  "or @userService.isCurrentUser(#ownerId)")
    @GetMapping("/create")
    public String createForm(Model model,
                             @PathVariable Long ownerId) {
        model.addAttribute("goal", new GoalRequest());
        model.addAttribute("ownerId", ownerId);
        return "goals/create-goal";
    }

    @PreAuthorize("hasAuthority('ADMIN') " +
                  "or @userService.isCurrentUser(#ownerId)")
    @PostMapping("/create")
    public String create(@PathVariable Long ownerId,
                         @Validated @ModelAttribute("goal") GoalRequest goalRequest,
                         BindingResult result,
                         Model model) {
        if (result.hasErrors()) {
            model.addAttribute("ownerId", ownerId);
            return "goals/create-goal";
        }

        goalService.create(ownerId, goalRequest);
        return "redirect:/users/" + ownerId + "/workouts/all";
    }

    @PreAuthorize("hasAuthority('ADMIN') " +
                  "or @userService.isCurrentUser(#ownerId)")
    @GetMapping("/{goalId}/read")
    public String read(@PathVariable Long ownerId,
                       @PathVariable Long goalId,
                       Model model) {
        Goal goal = goalService.readById(goalId);
        model.addAttribute("ownerId", ownerId);
        model.addAttribute("goal", goal);
        model.addAttribute("owner", goal.getOwner());
        return "goals/read-goal";
    }

    @PreAuthorize("hasAuthority('ADMIN') " +
                  "or @userService.isCurrentUser(#ownerId)")
    @GetMapping("/{goalId}/update")
    public String updateForm(@PathVariable Long ownerId,
                             @PathVariable Long goalId,
                             Model model) {
        model.addAttribute("goalId", goalId);
        model.addAttribute("goal", goalService.readById(goalId));
        model.addAttribute("ownerId", ownerId);
        return "goals/update-goal";
    }

    @PreAuthorize("hasAuthority('ADMIN') " +
                  "or @userService.isCurrentUser(#ownerId)")
    @PostMapping("/{goalId}/update")
    public String update(@PathVariable Long ownerId,
                         @PathVariable Long goalId,
                         @Validated @ModelAttribute("goal") GoalRequest goalRequest,
                         BindingResult result,
                         Model model) {
        if (result.hasErrors()) {
            model.addAttribute("ownerId", ownerId);
            model.addAttribute("goalId", goalId);
            model.addAttribute("goal", goalService.readById(goalId));
            return "goals/update-goal";
        }

        goalService.update(goalRequest, ownerId, goalService.readById(goalId));
        return "redirect:/users/" + ownerId + "/goals/" + goalId + "/read";
    }

    @PreAuthorize("hasAuthority('ADMIN') " +
                  "or @userService.isCurrentUser(#ownerId)")
    @PostMapping("{goalId}/delete")
    public String delete(@PathVariable Long ownerId,
                         @PathVariable Long goalId) {
        Goal goal = goalService.readById(goalId);
        goalService.deleteByEntityThrowing(goal, ownerId);
        return "redirect:/users/" + ownerId + "/goals/all";
    }

    @PreAuthorize("hasAuthority('ADMIN') " +
                  "or @userService.isCurrentUser(#ownerId)")
    @GetMapping("/all")
    public String getAllUserGoals(@PathVariable Long ownerId,
                                  Model model) {
        model.addAttribute("ownerId", ownerId);
        model.addAttribute("goals", goalService.getAllUsersGoals(ownerId));
        return "goals/goal-list";
    }

}
