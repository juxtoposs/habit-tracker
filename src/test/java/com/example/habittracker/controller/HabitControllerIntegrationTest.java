package com.example.habittracker.controller;

import com.example.habittracker.model.Habit;
import com.example.habittracker.repository.HabitRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for HabitController (Acceptance testing).
 */
@SpringBootTest
@AutoConfigureMockMvc
class HabitControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private HabitRepository habitRepository;

    @BeforeEach
    void setUp() {
        habitRepository.deleteAll();
    }

    @Test
    void createHabit_ShouldReturn201WithLinks() throws Exception {
        Habit habit = new Habit("Meditation", "10 minutes in the morning");

        mockMvc.perform(post("/api/habits")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(habit)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Meditation"))
                .andExpect(jsonPath("$._links.self.href").exists())
                .andExpect(jsonPath("$._links.logs.href").exists());
    }

    @Test
    void getAllHabits_ShouldReturnCollectionWithLinks() throws Exception {
        habitRepository.save(new Habit("Habit 1", "Description 1"));
        habitRepository.save(new Habit("Habit 2", "Description 2"));

        mockMvc.perform(get("/api/habits"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.habits", hasSize(2)))
                .andExpect(jsonPath("$._links.self.href").exists());
    }

    @Test
    void getHabitById_WhenExists_ShouldReturnHabitWithLinks() throws Exception {
        Habit saved = habitRepository.save(new Habit("Test", "Description"));

        mockMvc.perform(get("/api/habits/{id}", saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test"))
                .andExpect(jsonPath("$._links.self.href").exists())
                .andExpect(jsonPath("$._links.delete.href").exists());
    }

    @Test
    void getHabitById_WhenNotExists_ShouldReturn404() throws Exception {
        mockMvc.perform(get("/api/habits/{id}", 999L))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateHabit_ShouldReturnUpdatedHabit() throws Exception {
        Habit saved = habitRepository.save(new Habit("Old Name", "Old Description"));
        Habit updated = new Habit("New Name", "New Description");

        mockMvc.perform(put("/api/habits/{id}", saved.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("New Name"));
    }

    @Test
    void deleteHabit_ShouldReturn204() throws Exception {
        Habit saved = habitRepository.save(new Habit("To Delete", "This habit will be deleted"));

        mockMvc.perform(delete("/api/habits/{id}", saved.getId()))
                .andExpect(status().isNoContent());
    }
}
