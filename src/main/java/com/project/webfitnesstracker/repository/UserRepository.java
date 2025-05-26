package com.project.webfitnesstracker.repository;

import com.project.webfitnesstracker.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
