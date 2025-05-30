package com.project.webfitnesstracker.controller;

import com.project.webfitnesstracker.dto.request.UserCreateRequest;
import com.project.webfitnesstracker.dto.request.UserUpdateRequest;
import com.project.webfitnesstracker.dto.response.UserResponse;
import com.project.webfitnesstracker.model.User;
import com.project.webfitnesstracker.model.UserRole;
import com.project.webfitnesstracker.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("user", new UserCreateRequest());
        return "register-user";
    }

    @PostMapping("/register")
    public String register(@Validated @ModelAttribute("user") UserCreateRequest request,
                         BindingResult result) {
        if (result.hasErrors()) {
            return "register-user";
        }
        userService.create(request);
        return "redirect:/login";
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    @GetMapping("/{id}/read")
    public String read(@PathVariable Long id,
                       Model model) {
        model.addAttribute("user", userService.readById(id));
        return "user-info";
    }

    @PreAuthorize("hasAuthority('ADMIN') " +
                  "or @userService.getAuthenticatedUser().id == #id")
    @PostMapping("/{id}/update")
    public String update(@PathVariable Long id,
                         Model model,
                         @Validated @ModelAttribute("user") UserUpdateRequest request,
                         BindingResult result) {
        UserResponse oldUser = userService.findByIdThrowing(id);
        User authUser = userService.getAuthenticatedUser();

        if (authUser.getRole() != UserRole.ADMIN) {
            request.setRole(oldUser.getRole());
        }

        if (result.hasErrors()) {
            model.addAttribute("roles", UserRole.values());
            return "update-user";
        }

        try {
            userService.update(id, request, authUser);
            return "redirect:/users/" + id + "/read";
        } catch (IllegalStateException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("roles", UserRole.values());
            return "update-user";
        }

    }

    @PreAuthorize("hasAuthority('ADMIN') " +
                  "or @userService.getAuthenticatedUser().id == #id")
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id,
                         HttpServletRequest request,
                         HttpServletResponse response) {
        User authUser = userService.getAuthenticatedUser();
        if (authUser.getId().equals(id)) {
            userService.delete(id);
            new SecurityContextLogoutHandler().logout(request, response, SecurityContextHolder.getContext().getAuthentication());
            return "redirect:/login?logout";
        }
        userService.delete(id);
        return "redirect:/users/all";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/all")
    public String getAll(Model model){
        model.addAttribute("users", userService.getAllUsers());
        return "users-list";
    }
}
