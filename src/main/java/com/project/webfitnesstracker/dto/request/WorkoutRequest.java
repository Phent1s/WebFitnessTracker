package com.project.webfitnesstracker.dto.request;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WorkoutRequest {
    @NotBlank(message = "Workout type required!")
    private String type;

    @NotNull(message = "Date required!")
    @FutureOrPresent(message = "Date can't be in the past!")
    private LocalDate date;

    @Min(value = 1, message = "Duration should be at least 1 minute!")
    private Integer durationInMinutes;

    @Min(value = 1, message = "Should be at least 1 calories")
    private Integer caloriesBurned;
}
