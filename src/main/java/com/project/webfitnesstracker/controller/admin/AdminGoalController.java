package com.project.webfitnesstracker.controller.admin;

import com.project.webfitnesstracker.service.GoalService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/goals")
@RequiredArgsConstructor
public class AdminGoalController {

    private final GoalService goalService;

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/all")
    public String getAll(Model model) {
        model.addAttribute("goals", goalService.getAllGoalsSortedByDate());
        return "goals/goal-list";
    }
}
