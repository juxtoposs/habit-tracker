package com.example.habittracker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main application class for Habit Tracker.
 * Launches the Spring Boot application with REST API and HATEOAS support.
 */
@SpringBootApplication
public class HabitTrackerApplication {

    /**
     * Application entry point.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(HabitTrackerApplication.class, args);
    }
}
