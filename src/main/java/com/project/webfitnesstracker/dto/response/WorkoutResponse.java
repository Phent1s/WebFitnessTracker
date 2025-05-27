package com.project.webfitnesstracker.dto.response;

import com.project.webfitnesstracker.model.Workout;
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

    public static WorkoutResponse fromEntity(Workout workout) {
        return WorkoutResponse.builder()
                .id(workout.getId())
                .type(workout.getType())
                .date(workout.getDate())
                .durationInMinutes(workout.getDurationInMinutes())
                .caloriesBurned(workout.getCaloriesBurned())
                .build();
    }


}
