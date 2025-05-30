package com.project.webfitnesstracker.service.mapper;

import com.project.webfitnesstracker.dto.request.GoalRequest;
import com.project.webfitnesstracker.dto.response.GoalResponse;
import com.project.webfitnesstracker.model.Goal;
import com.project.webfitnesstracker.model.User;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class GoalMapper {
    public Goal toEntity(@NotNull GoalRequest request,
                         @NotNull User owner){
        return Goal.builder()
                .description(request.getDescription())
                .targetValue(request.getTargetValue())
                .startDate(LocalDate.now())
                .endDate(request.getEndDate())
                .owner(owner)
                .build();
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
