package com.project.webfitnesstracker.repository;

import com.project.webfitnesstracker.model.Workout;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WorkoutRepository extends JpaRepository<Workout, Long> {

    List<Workout> findByOwner_IdOrderByDateAsc(Long id);

    List<Workout> findAllByOrderByDateAsc();



}
