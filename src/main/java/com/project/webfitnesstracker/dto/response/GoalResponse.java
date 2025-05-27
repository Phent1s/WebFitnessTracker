package com.project.webfitnesstracker.dto.response;

import com.project.webfitnesstracker.model.Goal;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class GoalResponse {
    private Long id;
    private String description;
    private BigDecimal targetValue;
    private BigDecimal currentValue;
    private LocalDate startDate;
    private LocalDate endDate;
    private boolean isAchieved;

    public static GoalResponse fromEntity(Goal goal) {
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
