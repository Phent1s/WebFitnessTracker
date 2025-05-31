package com.project.webfitnesstracker.service;

import com.project.webfitnesstracker.dto.request.WorkoutRequest;
import com.project.webfitnesstracker.dto.response.WorkoutResponse;
import com.project.webfitnesstracker.model.User;
import com.project.webfitnesstracker.model.Workout;
import com.project.webfitnesstracker.repository.WorkoutRepository;
import com.project.webfitnesstracker.security.exception.NullEntityReferenceException;
import com.project.webfitnesstracker.service.mapper.WorkoutMapper;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class WorkoutServiceTest {

    @Mock
    private WorkoutRepository workoutRepository;

    @Mock
    private UserService userService;

    @Mock
    private WorkoutMapper workoutMapper;

    @InjectMocks
    private WorkoutService workoutService;

    private WorkoutRequest workoutRequest;
    private Workout workout;
    private User owner;

    @BeforeEach
    void setUp(){
        owner = new User();
        owner.setId(1L);
        owner.setUsername("owner");

        workoutRequest = new WorkoutRequest();
        workout = new Workout();
        workout.setId(1L);
        workout.setOwner(owner);
    }

    @Test
    void create_shouldSaveWorkout() {

        when(userService.findById(1L)).thenReturn(owner);
        when(workoutMapper.toEntity(workoutRequest, owner)).thenReturn(workout);
        when(workoutRepository.save(workout)).thenReturn(workout);

        Workout result = workoutService.create(1L, workoutRequest);

        assertNotNull(result);
        assertEquals(workout, result);
    }

    @Test
    void create_shouldThrowIfRequestIsNull(){
        assertThrows(NullEntityReferenceException.class, () -> workoutService.create(1L, null));
    }

    @Test
    void update_shouldUpdateWorkout() {

        when(userService.findById(1L)).thenReturn(owner);
        when(workoutMapper.toExistingEntity(workoutRequest, owner, workout)).thenReturn(workout);

        workoutService.update(workoutRequest,1L,workout);

        verify(workoutRepository).save(workout);
    }

    @Test
    void update_shouldThrowAccessDeniedIfOwnerMismatch(){
        User otherUser = new User();
        otherUser.setId(2L);
        workout.setOwner(otherUser);

        assertThrows(AccessDeniedException.class,
                () -> workoutService.update(workoutRequest, 1L, workout));
    }

    @Test
    void delete_shouldDeleteWorkoutIfOwnerMatches(){
        workout.setOwner(owner);

        workoutService.deleteByEntityThrowing(workout, 1L);

        verify(workoutRepository).delete(workout);
    }

    @Test
    void delete_shouldThrowAccessDeniedIfOwnerMismatch(){
        User otherUser = new User();
        otherUser.setId(2L);
        workout.setOwner(otherUser);

        assertThrows(AccessDeniedException.class,
                () -> workoutService.deleteByEntityThrowing(workout, 1L));
    }

    @Test
    void getAllUsersWorkouts_shouldReturnMappedResponses(){
        WorkoutResponse workoutResponse = WorkoutResponse.builder().build();

        when(workoutRepository.findByOwner_IdOrderByDateAsc(1L)).thenReturn(List.of(workout));
        when(workoutMapper.fromEntity(workout)).thenReturn(workoutResponse);

        List<WorkoutResponse> result = workoutService.getAllUsersWorkouts(1L);

        assertEquals(1, result.size());
        assertEquals(workoutResponse, result.get(0));
    }

    @Test
    void getAllWorkoutsSortedByDate_shouldReturnWorkouts(){

        when(workoutRepository.findAllByOrderByDateAsc()).thenReturn(List.of(workout));

        List<Workout> result = workoutService.getAllWorkoutsSortedByDate();

        assertEquals(1, result.size());
        assertEquals(workout, result.get(0));
    }

    @Test
    void readById_shouldReturnWorkout(){

        when(workoutRepository.findById(1L)).thenReturn(Optional.of(workout));

        Workout result = workoutService.readById(1L);

        assertEquals(workout, result);
    }

    @Test
    void readById_shouldThrowIfNotFound(){
        when(workoutRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> workoutService.readById(1L));
    }

    @Test
    void toRequest_shouldReturnMappedRequest(){

        when(workoutMapper.toRequest(workout)).thenReturn(workoutRequest);

        WorkoutRequest result = workoutService.toRequest(workout);

        assertEquals(workoutRequest, result);
    }
}
