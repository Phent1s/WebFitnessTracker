package com.project.webfitnesstracker.controller.admin;

import com.project.webfitnesstracker.dto.response.UserResponse;
import com.project.webfitnesstracker.model.User;
import com.project.webfitnesstracker.service.UserService;
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
public class AdminUserControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private Model model;

    @InjectMocks
    private AdminUserController adminUserController;

    @Test
    void getAll_ShouldReturnUsersListView() {
        List<UserResponse> users = Collections.singletonList(UserResponse.builder().build());
        when(userService.getAllUsersSortedById()).thenReturn(users);

        String viewName = adminUserController.getAll(model);

        assertEquals("users/users-list", viewName);
        verify(model).addAttribute("users", users);
        verify(userService).getAllUsersSortedById();
    }
}
