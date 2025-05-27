package com.project.webfitnesstracker.dto.response;

import com.project.webfitnesstracker.model.User;
import com.project.webfitnesstracker.model.UserRole;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserResponse {

    private Long id;
    private String username;
    private UserRole role;

    public static UserResponse fromEntity(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .role(user.getRole())
                .build();
    }
}
