package com.project.webfitnesstracker.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LoginControllerTest {
    @InjectMocks
    private LoginController loginController;

    @Test
    void login_WhenAnonymousUser_ShouldReturnLoginPage() {
        Authentication auth = new AnonymousAuthenticationToken(
                "key", "anonymousUser", AuthorityUtils.createAuthorityList("ROLE_ANONYMOUS"));
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(securityContext);

        String result = loginController.login();

        assertEquals("login", result);
    }

    @Test
    void login_WhenAuthenticatedUser_ShouldRedirectToHome() {
        // Настраиваем аутентифицированного пользователя
        Authentication auth = mock(Authentication.class);
        when(auth.getPrincipal()).thenReturn("authenticatedUser");
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(securityContext);

        String result = loginController.login();

        assertEquals("redirect:/", result);
    }
}
