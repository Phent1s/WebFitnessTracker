package com.project.webfitnesstracker.service;

import com.project.webfitnesstracker.dto.request.GoalRequest;
import com.project.webfitnesstracker.dto.response.GoalResponse;
import com.project.webfitnesstracker.model.Goal;
import com.project.webfitnesstracker.repository.GoalRepository;
import com.project.webfitnesstracker.security.exception.NullEntityReferenceException;
import com.project.webfitnesstracker.service.mapper.GoalMapper;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GoalService {

    private final GoalRepository goalRepository;
    public final UserService userService;
    private final GoalMapper goalMapper;

    public Goal create(Long ownerId,
                       GoalRequest goalRequest) {
        if (goalRequest == null){
            throw new NullEntityReferenceException("Goal cannot be 'null'");
        }
        return goalRepository.save(goalMapper.toEntity(goalRequest, userService.findById(ownerId)));
    }

    public void update(GoalRequest goalRequest,
                       Long ownerId,
                       Goal existingGoal) {
        if (goalRequest == null){
            throw new NullEntityReferenceException("Goal cannot be 'null'");
        }
        if (!existingGoal.getOwner().getId().equals(ownerId)) {
            throw new AccessDeniedException("Goal does not belong to this user");
        }
        goalRepository.save(goalMapper.toExistingEntity(goalRequest, userService.findById(ownerId), existingGoal));
    }

    public void deleteByEntityThrowing(Goal goal,
                                       Long ownerId){
        if (!goal.getOwner().getId().equals(ownerId)) {
            throw new AccessDeniedException("Goal does not belong to this user");
        }
        goalRepository.delete(goal);
    }

    public List<GoalResponse> getAllUsersGoals(Long ownerId) {
        List<Goal> goals = goalRepository.findByOwner_IdOrderByStartDateAsc(ownerId);
        return goals.stream()
                .map(goalMapper::fromEntity)
                .collect(Collectors.toList());
    }

    public List<GoalResponse> getAllGoalsSortedByDate() {
        List<Goal> goals = goalRepository.findAllByOrderByStartDateAsc();
        return goals.stream()
                .map(goalMapper::fromEntity)
                .collect(Collectors.toList());
    }

    public Goal readById(Long goalId) {
        return goalRepository.findById(goalId)
                .orElseThrow(() -> new EntityNotFoundException("Goal with id " + goalId + " not found"));
    }
}
