package com.project.webfitnesstracker.service;

import com.project.webfitnesstracker.dto.request.UserCreateRequest;
import com.project.webfitnesstracker.dto.request.UserUpdateRequest;
import com.project.webfitnesstracker.dto.response.UserResponse;
import com.project.webfitnesstracker.model.User;
import com.project.webfitnesstracker.model.UserRole;
import com.project.webfitnesstracker.repository.UserRepository;
import com.project.webfitnesstracker.security.exception.NullEntityReferenceException;
import com.project.webfitnesstracker.service.mapper.UserMapper;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User adminUser;
    private User normalUser;
    private UserUpdateRequest updateRequest;

    @BeforeEach
    void setUp(){
        adminUser = new User();
        adminUser.setId(1L);
        adminUser.setUsername("admin");
        adminUser.setRole(UserRole.ADMIN);

        normalUser = new User();
        normalUser.setId(2L);
        normalUser.setUsername("user");
        normalUser.setRole(UserRole.USER);

        updateRequest = new UserUpdateRequest();
        updateRequest.setUsername("newName");
        updateRequest.setRole(UserRole.USER);

    }

    @Test
    void create_shouldSaveUser_whenValidRequest(){

        UserCreateRequest request = new UserCreateRequest();
        request.setUsername("testUser");
        request.setPassword("testPass123");
        request.setRole(UserRole.USER);

        User userEntity = new User();
        userEntity.setUsername("testUser");
        userEntity.setPassword("encodedPassword");
        userEntity.setRole(UserRole.USER);

        when(passwordEncoder.encode("testPass123")).thenReturn("encodedPassword");
        when(userMapper.toEntity(request)).thenReturn(userEntity);
        when(userRepository.save(userEntity)).thenReturn(userEntity);


        User result = userService.create(request);

        assertNotNull(result);
        assertEquals("testUser", result.getUsername());
        assertEquals("encodedPassword", result.getPassword());

        verify(passwordEncoder).encode("testPass123");
        verify(userMapper).toEntity(request);
        verify(userRepository).save(userEntity);
    }

    @Test
    void create_shouldThrowException_whenRequestIsNull(){
        assertThrows(NullEntityReferenceException.class, () -> userService.create(null));
    }

    @Test
    void update_shouldUpdateUser_whenCalledByAdmin(){
        when(userRepository.findById(2L)).thenReturn(Optional.of(normalUser));
        userService.update(2L, updateRequest, adminUser);

        assertEquals("newName", normalUser.getUsername());
        assertEquals(UserRole.USER, normalUser.getRole());
        verify(userRepository).save(normalUser);
    }

    @Test
    void update_ShouldThrowException_whenUsernameIsAlreadyExist(){
        updateRequest.setUsername("admin");
        when(userRepository.findById(2L)).thenReturn(Optional.of(normalUser));
        when(userRepository.findByUsername(updateRequest.getUsername())).thenReturn(Optional.of(adminUser));

        EntityExistsException exception = assertThrows(EntityExistsException.class,
                () -> userService.update(2L, updateRequest, normalUser));
        assertEquals("User with username admin already exists", exception.getMessage());
        verify(userRepository, never()).save(any());

    }

    @Test
    void update_ShouldThrowException_whenUserIsNotAdminAndTriesToChangeRole(){
        updateRequest.setRole(UserRole.ADMIN);
        when(userRepository.findById(2L)).thenReturn(Optional.of(normalUser));

        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> userService.update(2L, updateRequest, normalUser));
        assertEquals("Only admins can change user roles", exception.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    void update_shouldThrowException_whenAdminTriesToChangeOwnRole(){
        when(userRepository.findById(1L)).thenReturn(Optional.of(adminUser));

        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> userService.update(1L, updateRequest, adminUser));
        assertEquals("Admin cannot change his own role!", exception.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    void getAllUsersSortedById_shouldReturnMappedResponses(){
        User user1 = new User();
        user1.setId(1L);
        user1.setUsername("user1");

        User user2 = new User();
        user2.setId(2L);
        user2.setUsername("user2");

        when(userRepository.findAllByOrderByIdAsc()).thenReturn(List.of(user1, user2));

        UserResponse response1 = UserResponse.builder().id(1L).build();
        UserResponse response2 = UserResponse.builder().id(2L).build();

        when(userMapper.fromEntity(user1)).thenReturn(response1);
        when(userMapper.fromEntity(user2)).thenReturn(response2);

        List<UserResponse> result = userService.getAllUsersSortedById();

        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals(2L, result.get(1).getId());
    }

    @Test
    void isCurrentUser_shouldReturnTrue_whenUserMatchesAuthenticated(){
        User authUser = new User();
        authUser.setId(1L);

        try(MockedStatic<SecurityContextHolder> contextMock = Mockito.mockStatic(SecurityContextHolder.class)) {
            SecurityContext context = mock(SecurityContext.class);
            Authentication auth = mock(Authentication.class);

            contextMock.when(SecurityContextHolder::getContext).thenReturn(context);
            when(context.getAuthentication()).thenReturn(auth);
            when(auth.getPrincipal()).thenReturn(authUser);

            assertTrue(userService.isCurrentUser(1L));
            assertFalse(userService.isCurrentUser(2L));
        }
    }

    @Test
    void findByIdThrowing_shouldReturnResponse_whenUserExists(){
        User user = new User();
        user.setId(1L);

        UserResponse response = UserResponse.builder().id(1L).build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userMapper.fromEntity(user)).thenReturn(response);

        UserResponse result = userService.findByIdThrowing(1L);

        assertEquals(1L, result.getId());
    }

    @Test
    void findByIdThrowing_shouldThrowException_whenUserNotFound(){
        when(userRepository.findById(52L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> userService.findByIdThrowing(52L));
    }

    @Test
    void readById_shouldReturnUser_whenUserExist(){
        User userEntity = new User();
        userEntity.setId(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(userEntity));

        User result = userService.readById(1L);
        assertEquals(1L, result.getId());
    }

    @Test
    void readById_shouldThrowException_whenUserDoesNotExist(){
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> userService.readById(1L));
    }

    @Test
    void delete_shouldCallRepositoryDelete_whenUserExist(){
        User userEntity = new User();
        userEntity.setId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(userEntity));

        userService.delete(1L);
        verify(userRepository).delete(userEntity);
    }
}
