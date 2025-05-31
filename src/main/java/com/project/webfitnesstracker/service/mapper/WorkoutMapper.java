package com.project.webfitnesstracker.service.mapper;

import com.project.webfitnesstracker.dto.request.WorkoutRequest;
import com.project.webfitnesstracker.dto.response.WorkoutResponse;
import com.project.webfitnesstracker.model.User;
import com.project.webfitnesstracker.model.Workout;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;

@Service
public class WorkoutMapper {

    public Workout toEntity(@NotNull WorkoutRequest request,
                            @NotNull User owner) {
        return Workout.builder()
                .type(request.getType())
                .date(request.getDate())
                .durationInMinutes(request.getDurationInMinutes())
                .caloriesBurned(request.getCaloriesBurned())
                .owner(owner)
                .build();
    }

    public Workout toExistingEntity(@NotNull WorkoutRequest request,
                                    @NotNull User owner,
                                    @NotNull Workout existingWorkout) {
        existingWorkout.setType(request.getType());
        existingWorkout.setDate(request.getDate());
        existingWorkout.setDurationInMinutes(request.getDurationInMinutes());
        existingWorkout.setCaloriesBurned(request.getCaloriesBurned());
        existingWorkout.setOwner(owner);
        return existingWorkout;

    }

    public WorkoutRequest toRequest(Workout workout) {
        return WorkoutRequest.builder()
                .type(workout.getType())
                .date(workout.getDate())
                .durationInMinutes(workout.getDurationInMinutes())
                .caloriesBurned(workout.getCaloriesBurned())
                .build();
    }

    public WorkoutResponse fromEntity(@NotNull Workout workout) {
        return WorkoutResponse.builder()
                .id(workout.getId())
                .type(workout.getType())
                .date(workout.getDate())
                .durationInMinutes(workout.getDurationInMinutes())
                .caloriesBurned(workout.getCaloriesBurned())
                .build();
    }
}
