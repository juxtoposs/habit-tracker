package com.example.habittracker.controller;

import com.example.habittracker.model.Habit;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import java.time.LocalDateTime;

/**
 * HATEOAS model for representing a habit in the REST API.
 * Extends RepresentationModel for automatic link addition (_links).
 */
@Relation(value = "habit", collectionRelation = "habits")
public class HabitModel extends RepresentationModel<HabitModel> {

    private Long id;
    private String name;
    private String description;
    private LocalDateTime createdAt;

    /**
     * Default constructor.
     */
    public HabitModel() {
    }

    /**
     * Creates a model from a Habit entity.
     *
     * @param habit habit entity
     */
    public HabitModel(Habit habit) {
        this.id = habit.getId();
        this.name = habit.getName();
        this.description = habit.getDescription();
        this.createdAt = habit.getCreatedAt();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
