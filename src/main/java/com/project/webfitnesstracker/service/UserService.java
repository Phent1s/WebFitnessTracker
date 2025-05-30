package com.project.webfitnesstracker.service;

import com.project.webfitnesstracker.dto.request.UserCreateRequest;
import com.project.webfitnesstracker.dto.request.UserUpdateRequest;
import com.project.webfitnesstracker.dto.response.UserResponse;
import com.project.webfitnesstracker.model.User;
import com.project.webfitnesstracker.model.UserRole;
import com.project.webfitnesstracker.repository.UserRepository;
import com.project.webfitnesstracker.security.exception.NullEntityReferenceException;
import com.project.webfitnesstracker.service.mapper.UserMapper;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User with username " + username + " not found"));
    }

    public User readById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User with id " + id + " not found"));
    }

    public User create(UserCreateRequest userCreateRequest) {
        if (userCreateRequest == null) {
            throw new NullEntityReferenceException("User cannot be 'null'");
        }

        userCreateRequest.setPassword(passwordEncoder.encode(userCreateRequest.getPassword()));
        return userRepository.save(userMapper.toEntity(userCreateRequest));
    }

    public void update(Long id, UserUpdateRequest userUpdateRequest, User authenticatedUser) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User with id " + id + " not found"));

        if (authenticatedUser.getId().equals(id)
            && authenticatedUser.getRole() == UserRole.ADMIN
            && userUpdateRequest.getRole() != authenticatedUser.getRole()) {
            throw new IllegalStateException("Admin cannot change his own role!");
        }

        if (authenticatedUser.getRole() == UserRole.ADMIN && !authenticatedUser.getId().equals(id)) {
            user.setRole(userUpdateRequest.getRole());
        } else if (userUpdateRequest.getRole() != user.getRole()) {
            throw new IllegalStateException("Only admins can change roles of other users");
        }

        updateUser(user, userUpdateRequest);
        userRepository.save(user);
    }

    public void delete(Long id) {
        User user = readById(id);
        userRepository.delete(user);
    }

    public List<UserResponse> getAllUsers() {
        List<User> users = userRepository.findAllByOrderByIdAsc();
        return users.stream()
                .map(userMapper::fromEntity)
                .collect(Collectors.toList());
    }

    private void updateUser(User user, UserUpdateRequest userUpdateRequest) {
        user.setUsername(userUpdateRequest.getUsername());
        user.setRole(userUpdateRequest.getRole());
    }

    public User getAuthenticatedUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    public UserResponse findByIdThrowing(Long id) {

        return userRepository.findById(id).map(userMapper::fromEntity).orElseThrow(EntityNotFoundException::new);
    }
}
