package com.project.webfitnesstracker.repository;

import com.project.webfitnesstracker.model.Workout;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkoutRepository extends JpaRepository<Workout, Long> {
}
