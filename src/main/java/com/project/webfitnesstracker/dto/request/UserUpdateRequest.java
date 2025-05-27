package com.project.webfitnesstracker.dto.request;

import com.project.webfitnesstracker.model.UserRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserUpdateRequest {

    @Size(min = 3, max = 50, message = "Username should be from 3 to 50 digits")
    @NotBlank(message = "Username can't be empty!")
    private String username;

    @NotNull
    private UserRole role;
}
