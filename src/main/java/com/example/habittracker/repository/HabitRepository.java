package com.example.habittracker.repository;

import com.example.habittracker.model.Habit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for accessing Habit data.
 * Provides standard CRUD operations via Spring Data JPA.
 */
@Repository
public interface HabitRepository extends JpaRepository<Habit, Long> {
}
