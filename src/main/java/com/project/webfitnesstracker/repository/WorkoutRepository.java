package com.project.webfitnesstracker.repository;

import com.project.webfitnesstracker.model.Workout;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkoutRepository extends JpaRepository<Workout, Long> {
}
