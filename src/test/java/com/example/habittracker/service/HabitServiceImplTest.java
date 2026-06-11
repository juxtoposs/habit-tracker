package com.example.habittracker.service;

import com.example.habittracker.model.Habit;
import com.example.habittracker.repository.HabitRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for HabitServiceImpl.
 */
@ExtendWith(MockitoExtension.class)
class HabitServiceImplTest {

    @Mock
    private HabitRepository habitRepository;

    @InjectMocks
    private HabitServiceImpl habitService;

    private Habit testHabit;

    @BeforeEach
    void setUp() {
        testHabit = new Habit("Reading", "Read 30 minutes a day");
        testHabit.setId(1L);
    }

    @Test
    void findAll_ShouldReturnAllHabits() {
        when(habitRepository.findAll()).thenReturn(List.of(testHabit));

        List<Habit> result = habitService.findAll();

        assertEquals(1, result.size());
        assertEquals("Reading", result.get(0).getName());
        verify(habitRepository, times(1)).findAll();
    }

    @Test
    void findById_WhenExists_ShouldReturnHabit() {
        when(habitRepository.findById(1L)).thenReturn(Optional.of(testHabit));

        Optional<Habit> result = habitService.findById(1L);

        assertTrue(result.isPresent());
        assertEquals("Reading", result.get().getName());
    }

    @Test
    void findById_WhenNotExists_ShouldReturnEmpty() {
        when(habitRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<Habit> result = habitService.findById(99L);

        assertFalse(result.isPresent());
    }

    @Test
    void create_ShouldSaveAndReturnHabit() {
        when(habitRepository.save(any(Habit.class))).thenReturn(testHabit);

        Habit result = habitService.create(new Habit("Reading", "Read 30 minutes"));

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(habitRepository, times(1)).save(any(Habit.class));
    }

    @Test
    void update_WhenExists_ShouldUpdateAndReturn() {
        Habit updatedDetails = new Habit("Sports", "Morning run");
        when(habitRepository.findById(1L)).thenReturn(Optional.of(testHabit));
        when(habitRepository.save(any(Habit.class))).thenReturn(testHabit);

        Habit result = habitService.update(1L, updatedDetails);

        assertEquals("Sports", result.getName());
        assertEquals("Morning run", result.getDescription());
    }

    @Test
    void update_WhenNotExists_ShouldThrowException() {
        when(habitRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> habitService.update(99L, new Habit("Test", "Description")));
    }

    @Test
    void delete_WhenExists_ShouldDelete() {
        when(habitRepository.existsById(1L)).thenReturn(true);

        habitService.delete(1L);

        verify(habitRepository, times(1)).deleteById(1L);
    }

    @Test
    void delete_WhenNotExists_ShouldThrowException() {
        when(habitRepository.existsById(99L)).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> habitService.delete(99L));
    }
}
