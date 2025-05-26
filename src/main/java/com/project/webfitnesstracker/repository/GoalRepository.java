package com.project.webfitnesstracker.repository;

import com.project.webfitnesstracker.model.Goal;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GoalRepository extends JpaRepository<Goal, Long> {
}
