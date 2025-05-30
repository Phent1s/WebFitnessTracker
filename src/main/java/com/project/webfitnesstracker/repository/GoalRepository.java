package com.project.webfitnesstracker.repository;

import com.project.webfitnesstracker.model.Goal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GoalRepository extends JpaRepository<Goal, Long> {
    List<Goal> findByOwner_IdOrderByStartDateAsc(Long id);

    List<Goal> findAllByOrderByStartDateAsc();

}
