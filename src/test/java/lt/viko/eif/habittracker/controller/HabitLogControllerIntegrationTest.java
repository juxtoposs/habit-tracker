package lt.viko.eif.habittracker.controller;

import lt.viko.eif.habittracker.model.Habit;
import lt.viko.eif.habittracker.model.HabitLog;
import lt.viko.eif.habittracker.repository.HabitLogRepository;
import lt.viko.eif.habittracker.repository.HabitRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class HabitLogControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private HabitRepository habitRepository;

    @Autowired
    private HabitLogRepository habitLogRepository;

    private Habit savedHabit;
    private LocalDate today;

    @BeforeEach
    void setUp() {
        habitLogRepository.deleteAll();
        habitRepository.deleteAll();

        savedHabit = habitRepository.save(new Habit("Workout", "Gym session"));
        today = LocalDate.now();
    }

    @Test
    void getLogsForHabit_ShouldReturnCollectionWithLinks() throws Exception {
        HabitLog log1 = new HabitLog(today.minusDays(1));
        log1.setHabit(savedHabit);
        habitLogRepository.save(log1);

        mockMvc.perform(get("/api/habits/{habitId}/logs", savedHabit.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.logs", hasSize(1)))
                .andExpect(jsonPath("$._links.self.href").exists())
                .andExpect(jsonPath("$._links.habit.href").exists());
    }

    @Test
    void markCompleted_ShouldReturn201WithLinks() throws Exception {
        mockMvc.perform(post("/api/habits/{habitId}/logs", savedHabit.getId())
                        .param("completedDate", today.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.completedDate").value(today.toString()))
                .andExpect(jsonPath("$._links.logs.href").exists())
                .andExpect(jsonPath("$._links.delete.href").exists());
    }

    @Test
    void markCompleted_WhenDuplicate_ShouldReturn409() throws Exception {
        HabitLog log = new HabitLog(today);
        log.setHabit(savedHabit);
        habitLogRepository.save(log);

        mockMvc.perform(post("/api/habits/{habitId}/logs", savedHabit.getId())
                        .param("completedDate", today.toString()))
                .andExpect(status().isConflict());
    }

    @Test
    void deleteLog_ShouldReturn204() throws Exception {
        HabitLog log = new HabitLog(today);
        log.setHabit(savedHabit);
        HabitLog savedLog = habitLogRepository.save(log);

        mockMvc.perform(delete("/api/habits/{habitId}/logs/{logId}", savedHabit.getId(), savedLog.getId()))
                .andExpect(status().isNoContent());
    }
}