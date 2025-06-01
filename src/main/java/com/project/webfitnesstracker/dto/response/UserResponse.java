package com.project.webfitnesstracker.dto.response;

import com.project.webfitnesstracker.model.UserRole;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserResponse {

    private Long id;
    private String username;
    private UserRole role;
}
