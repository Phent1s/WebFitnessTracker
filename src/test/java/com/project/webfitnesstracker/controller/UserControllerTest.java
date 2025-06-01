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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private Model model;

    @Mock
    private BindingResult bindingResult;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private SecurityContextLogoutHandler logoutHandler;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private UserController userController;

    private UserCreateRequest createRequest;
    private UserUpdateRequest updateRequest;
    private UserResponse userResponse;
    private User authUser;

    @BeforeEach
    void setUp(){
        createRequest = new UserCreateRequest();
        createRequest.setUsername("testuser");
        createRequest.setPassword("password123");
        createRequest.setRole(UserRole.USER);

        updateRequest = new UserUpdateRequest();
        updateRequest.setUsername("updateduser");
        updateRequest.setRole(UserRole.USER);

        userResponse = UserResponse.builder()
                .id(1L)
                .username("testuser")
                .role(UserRole.USER)
                .build();

        authUser = new User();
        authUser.setId(1L);
        authUser.setUsername("testuser");
        authUser.setRole(UserRole.USER);

        lenient().when(userService.getAuthenticatedUser()).thenReturn(authUser);
        SecurityContextHolder.setContext(securityContext);
        lenient().when(securityContext.getAuthentication()).thenReturn(authentication);
        lenient().when(authentication.getPrincipal()).thenReturn(authUser);
    }

    @Test
    void register_ShouldReturnRegisterForm(){
        String viewName = userController.register(model);

        assertEquals("users/register-user", viewName);
        verify(model).addAttribute(eq("user"),any(UserCreateRequest.class));
    }

    @Test
    void register_WithValidInput_ShouldRedirectToLogin(){
        when(bindingResult.hasErrors()).thenReturn(false);
        when(userService.create(createRequest)).thenReturn(authUser);

        String viewName = userController.register(createRequest, bindingResult);

        assertEquals("redirect:/login", viewName);
        verify(userService).create(createRequest);
    }

    @Test
    void register_WithBindingErrors_ShouldReturnRegisterForm(){
        when(bindingResult.hasErrors()).thenReturn(true);

        String viewName = userController.register(createRequest, bindingResult);

        assertEquals("users/register-user", viewName);
        verify(userService, never()).create(any());
    }

    @Test
    void register_WithExistingUsername_ShouldReturnRegisterFormWithError(){
        when(bindingResult.hasErrors()).thenReturn(false);
        when(userService.create(createRequest)).thenThrow(new EntityExistsException("Username exists"));

        String viewName = userController.register(createRequest, bindingResult);

        assertEquals("users/register-user", viewName);
        verify(bindingResult).rejectValue("username", "error.user", "Username exists");
    }

    @Test
    void read_ShouldReturnUserInfoPage(){
        when(userService.readById(1L)).thenReturn(authUser);

        String viewName = userController.read(1L, model);

        assertEquals("users/user-info", viewName);
        verify(model).addAttribute("userId", 1L);
        verify(model).addAttribute("user", authUser);
    }

    @Test
    void updateForm_ForAdmin_ShouldReturnUpdateFormWithRoles(){
        authUser.setRole(UserRole.ADMIN);
        when(userService.readById(1L)).thenReturn(authUser);

        String viewName = userController.updateForm(1L, model);

        assertEquals("users/update-user", viewName);
        verify(model).addAttribute("userId", 1L);
        verify(model).addAttribute("user", authUser);
        verify(model).addAttribute("roles", UserRole.values());
    }

    @Test
    void updateForm_ForSelf_ShouldReturnUpdateForm(){
        when(userService.readById(1L)).thenReturn(authUser);

        String viewName = userController.updateForm(1L, model);

        assertEquals("users/update-user", viewName);
        verify(model).addAttribute("userId", 1L);
        verify(model).addAttribute("user", authUser);
        verify(model).addAttribute("roles", UserRole.values());
    }

    @Test
    void update_WithValidInput_ShouldRedirectToUserInfo(){
        when(bindingResult.hasErrors()).thenReturn(false);
        when(userService.findByIdThrowing(1L)).thenReturn(userResponse);

        String viewName = userController.update(1L, model, updateRequest, bindingResult);

        assertEquals("redirect:/users/1/read", viewName);
        verify(userService).update(eq(1L), eq(updateRequest), eq(authUser));
    }

    @Test
    void update_WithBindingErrors_ShouldReturnUpdateForm(){
        when(bindingResult.hasErrors()).thenReturn(true);

        String viewName = userController.update(1L, model, updateRequest, bindingResult);

        assertEquals("users/update-user", viewName);
        verify(model).addAttribute("userId", 1L);
        verify(model).addAttribute("user", updateRequest);
        verify(model).addAttribute("roles", UserRole.values());
        verify(userService, never()).update(any(), any(), any());
    }

    @Test
    void update_WithExistingUsername_ShouldReturnUpdateFormWithError(){
        when(bindingResult.hasErrors()).thenReturn(false);
        when(userService.findByIdThrowing(1L)).thenReturn(userResponse);
        doThrow(new EntityExistsException("Username exists"))
                .when(userService).update(eq(1L), eq(updateRequest), eq(authUser));

        String viewName = userController.update(1L, model, updateRequest, bindingResult);

        assertEquals("users/update-user", viewName);
        verify(bindingResult).rejectValue("username", "error.user", "Username exists");
        verify(model).addAttribute("userId", 1L);
        verify(model).addAttribute("user", updateRequest);
        verify(model).addAttribute("roles", UserRole.values());
    }


    @Test
    void delete_WhenDeletingSelf_ShouldLogoutAndRedirectToLogin(){
        when(userService.getAuthenticatedUser()).thenReturn(authUser);

        String viewName = userController.delete(1L, request, response);

        assertEquals("redirect:/login?logout", viewName);
        verify(userService).delete(1L);
    }

    @Test
    void delete_WhenAdminDeletesOtherUser_ShouldRedirectToAdminUsersPage(){
        authUser.setRole(UserRole.ADMIN);

        String viewName = userController.delete(2L, request, response);

        assertEquals("redirect:/admin/users/all", viewName);
        verify(userService).delete(2L);
        verify(logoutHandler, never()).logout(any(), any(), any());
    }
}
