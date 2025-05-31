package com.project.webfitnesstracker.controller;

import com.project.webfitnesstracker.dto.request.UserCreateRequest;
import com.project.webfitnesstracker.dto.request.UserUpdateRequest;
import com.project.webfitnesstracker.dto.response.UserResponse;
import com.project.webfitnesstracker.model.User;
import com.project.webfitnesstracker.model.UserRole;
import com.project.webfitnesstracker.service.UserService;
import jakarta.persistence.EntityExistsException;
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
        return "users/register-user";
    }

    @PostMapping("/register")
    public String register(@Validated @ModelAttribute("user") UserCreateRequest request,
                           BindingResult result) {
        if (result.hasErrors()) {
            return "users/register-user";
        }

        try {
            userService.create(request);
            return "redirect:/login";
        }catch (EntityExistsException ex){
            result.rejectValue("username", "error.user", ex.getMessage());
            return "users/register-user";
        }
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    @GetMapping("/{userId}/read")
    public String read(@PathVariable Long userId,
                       Model model) {
        model.addAttribute("userId", userId);
        model.addAttribute("user", userService.readById(userId));
        return "users/user-info";
    }

    @PreAuthorize("hasAuthority('ADMIN') " +
                  "or @userService.isCurrentUser(#userId)")
    @GetMapping("/{userId}/update")
    public String updateForm(@PathVariable Long userId,
                             Model model) {
        model.addAttribute("userId", userId);
        model.addAttribute("user", userService.readById(userId));
        model.addAttribute("roles", UserRole.values());
        return "users/update-user";
    }

    @PreAuthorize("hasAuthority('ADMIN') " +
                  "or @userService.isCurrentUser(#userId)")
    @PostMapping("/{userId}/update")
    public String update(@PathVariable Long userId,
                         Model model,
                         @Validated @ModelAttribute("user") UserUpdateRequest request,
                         BindingResult result) {
        UserResponse oldUser = userService.findByIdThrowing(userId);
        User authUser = userService.getAuthenticatedUser();
        if (result.hasErrors()) {
            model.addAttribute("userId", userId);
            model.addAttribute("user", request);
            model.addAttribute("roles", UserRole.values());
            return "users/update-user";
        }

        boolean isAdmin = authUser.getRole() == UserRole.ADMIN;

        if (!isAdmin) {
            request.setRole(oldUser.getRole());
        }

        try {
            userService.update(userId, request, authUser);
            return "redirect:/users/" + userId + "/read";
        } catch (IllegalStateException ex) {
            result.rejectValue("role", "error.role", ex.getMessage());
            model.addAttribute("userId", userId);
            model.addAttribute("user", request);
            model.addAttribute("roles", UserRole.values());
            return "users/update-user";
        } catch (EntityExistsException ex) {
            result.rejectValue("username", "error.user", ex.getMessage());
            model.addAttribute("userId", userId);
            model.addAttribute("user", request);
            model.addAttribute("roles", UserRole.values());
            return "users/update-user";
        }
    }

    @PreAuthorize("hasAuthority('ADMIN') " +
                  "or @userService.isCurrentUser(#userId)")
    @PostMapping("/{userId}/delete")
    public String delete(@PathVariable Long userId,
                         HttpServletRequest request,
                         HttpServletResponse response) {

        User authUser = userService.getAuthenticatedUser();

        boolean isSelf = authUser.getId().equals(userId);

        userService.delete(userId);

        if (isSelf) {
            new SecurityContextLogoutHandler()
                    .logout(request, response, SecurityContextHolder.getContext().getAuthentication());
            return "redirect:/login?logout";
        }
        return "redirect:/admin/users/all";
    }
}
