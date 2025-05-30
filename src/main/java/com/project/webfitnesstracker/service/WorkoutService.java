package com.project.webfitnesstracker.service;

import com.project.webfitnesstracker.dto.request.WorkoutRequest;
import com.project.webfitnesstracker.dto.response.WorkoutResponse;
import com.project.webfitnesstracker.model.Workout;
import com.project.webfitnesstracker.repository.WorkoutRepository;
import com.project.webfitnesstracker.security.exception.NullEntityReferenceException;
import com.project.webfitnesstracker.service.mapper.WorkoutMapper;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WorkoutService {

    private final WorkoutRepository workoutRepository;
    private final UserService userService;
    private final WorkoutMapper workoutMapper;

    public Workout create(Long ownerId,
                          WorkoutRequest request){
        if (request == null){
            throw new NullEntityReferenceException("Workout cannot be null");
        }
        return workoutRepository.save(workoutMapper.toEntity(request, userService.findById(ownerId)));
    }

    public void update(WorkoutRequest request,
                       Long ownerId,
                       Workout existingWorkout){
        if (request == null){
            throw new NullEntityReferenceException("Workout cannot be null");
        }
        if (!existingWorkout.getOwner().getId().equals(ownerId)) {
            throw new AccessDeniedException("Workout does not belong to this user");
        }
        workoutRepository.save(workoutMapper.toExistingEntity(request, userService.findById(ownerId), existingWorkout));
    }

    public void deleteByEntityThrowing(Workout workout,
                                       Long ownerId){
        if (!workout.getOwner().getId().equals(ownerId)) {
            throw new AccessDeniedException("Goal does not belong to this user");
        }
        workoutRepository.delete(workout);
    }

    public List<WorkoutResponse> getAllUsersWorkouts(Long ownerId){
        List<Workout> workouts = workoutRepository.findByOwner_IdOrderByDateAsc(ownerId);
        return workouts.stream()
                .map(workoutMapper::fromEntity)
                .collect(Collectors.toList());
    }

    public List<WorkoutResponse> getAllWorkoutsSortedByDate(){
        List<Workout> workouts = workoutRepository.findAllByOrderByDateAsc();
        return workouts.stream()
                .map(workoutMapper::fromEntity)
                .collect(Collectors.toList());
    }

    public Workout readById(Long workoutId){
        return workoutRepository.findById(workoutId)
                .orElseThrow(() -> new EntityNotFoundException("Workout with id " + workoutId + " not found"));
    }


}
