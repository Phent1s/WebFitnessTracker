package com.project.webfitnesstracker.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GoalRequest {

    @NotBlank(message = "Description can't be empty!")
    private String description;

    @DecimalMin(value = "0.01", message = "Target Value should be at least 0.01!")
    private BigDecimal targetValue;

    @NotNull
    @FutureOrPresent(message = "End date can't be in the past!")
    private LocalDate endDate;
}
