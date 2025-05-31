package com.project.webfitnesstracker.service.mapper;

import com.project.webfitnesstracker.dto.request.GoalRequest;
import com.project.webfitnesstracker.dto.response.GoalResponse;
import com.project.webfitnesstracker.model.Goal;
import com.project.webfitnesstracker.model.User;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
public class GoalMapper {
    public Goal toEntity(@NotNull GoalRequest request,
                         @NotNull User owner){
        return Goal.builder()
                .description(request.getDescription())
                .targetValue(request.getTargetValue())
                .currentValue(BigDecimal.ZERO)
                .startDate(LocalDate.now())
                .endDate(request.getEndDate())
                .owner(owner)
                .build();
    }

    public GoalRequest toRequest(@NotNull Goal goal){
        return GoalRequest.builder()
                .description(goal.getDescription())
                .targetValue(goal.getTargetValue())
                .currentValue(goal.getCurrentValue())
                .endDate(goal.getEndDate())
                .build();
    }

    public Goal toExistingEntity(@NotNull GoalRequest request,
                                 @NotNull User owner,
                                 @NotNull Goal existingGoal){
        existingGoal.setDescription(request.getDescription());
        existingGoal.setTargetValue(request.getTargetValue());
        existingGoal.setCurrentValue(request.getCurrentValue());
        existingGoal.setEndDate(request.getEndDate());
        existingGoal.setOwner(owner);
        if (request.getCurrentValue().compareTo(existingGoal.getTargetValue()) >= 0){
            existingGoal.setAchieved(true);
        }
        return existingGoal;
    }

    public GoalResponse fromEntity(@NotNull Goal goal) {
        return GoalResponse.builder()
                .id(goal.getId())
                .description(goal.getDescription())
                .targetValue(goal.getTargetValue())
                .currentValue(goal.getCurrentValue())
                .startDate(goal.getStartDate())
                .endDate(goal.getEndDate())
                .isAchieved(goal.isAchieved())
                .build();
    }
}
