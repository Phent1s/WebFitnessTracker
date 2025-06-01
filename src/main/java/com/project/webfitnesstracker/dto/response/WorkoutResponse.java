package com.project.webfitnesstracker.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class WorkoutResponse {
    private Long id;
    private String type;
    private LocalDate date;
    private Integer durationInMinutes;
    private Integer caloriesBurned;
}
