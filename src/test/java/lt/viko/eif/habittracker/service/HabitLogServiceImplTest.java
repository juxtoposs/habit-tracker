package lt.viko.eif.habittracker.service;

import lt.viko.eif.habittracker.exception.DuplicateResourceException;
import lt.viko.eif.habittracker.exception.ResourceNotFoundException;
import lt.viko.eif.habittracker.model.Habit;
import lt.viko.eif.habittracker.model.HabitLog;
import lt.viko.eif.habittracker.repository.HabitLogRepository;
import lt.viko.eif.habittracker.repository.HabitRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HabitLogServiceImplTest {

    @Mock
    private HabitLogRepository habitLogRepository;

    @Mock
    private HabitRepository habitRepository;

    @InjectMocks
    private HabitLogServiceImpl habitLogService;

    private Habit testHabit;
    private HabitLog testLog;
    private LocalDate today;

    @BeforeEach
    void setUp() {
        today = LocalDate.now();
        testHabit = new Habit("Meditation", "10 min");
        testHabit.setId(1L);

        testLog = new HabitLog(today);
        testLog.setId(10L);
        testLog.setHabit(testHabit);
    }

    @Test
    void findByHabitId_WhenHabitExists_ShouldReturnLogs() {
        when(habitRepository.existsById(1L)).thenReturn(true);
        when(habitLogRepository.findByHabitId(1L)).thenReturn(List.of(testLog));

        List<HabitLog> result = habitLogService.findByHabitId(1L);
        assertEquals(1, result.size());
        assertEquals(today, result.get(0).getCompletedDate());
    }

    @Test
    void findByHabitId_WhenHabitDoesNotExist_ShouldThrowException() {
        when(habitRepository.existsById(99L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> habitLogService.findByHabitId(99L));
    }

    @Test
    void markCompleted_WhenValid_ShouldSaveLog() {
        when(habitRepository.findById(1L)).thenReturn(Optional.of(testHabit));
        when(habitLogRepository.existsByHabitIdAndCompletedDate(1L, today)).thenReturn(false);
        when(habitLogRepository.save(any(HabitLog.class))).thenReturn(testLog);

        HabitLog result = habitLogService.markCompleted(1L, today);
        assertNotNull(result);
        assertEquals(today, result.getCompletedDate());
        verify(habitLogRepository, times(1)).save(any(HabitLog.class));
    }

    @Test
    void markCompleted_WhenAlreadyLogged_ShouldThrowDuplicateException() {
        when(habitRepository.findById(1L)).thenReturn(Optional.of(testHabit));
        when(habitLogRepository.existsByHabitIdAndCompletedDate(1L, today)).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> habitLogService.markCompleted(1L, today));
        verify(habitLogRepository, never()).save(any(HabitLog.class));
    }

    @Test
    void deleteLog_WhenValid_ShouldDelete() {
        when(habitRepository.existsById(1L)).thenReturn(true);
        when(habitLogRepository.findByIdAndHabit_Id(10L, 1L)).thenReturn(Optional.of(testLog));

        habitLogService.deleteLog(1L, 10L);
        verify(habitLogRepository, times(1)).delete(testLog);
    }

    @Test
    void deleteLog_WhenLogNotFoundForHabit_ShouldThrowException() {
        when(habitRepository.existsById(1L)).thenReturn(true);
        when(habitLogRepository.findByIdAndHabit_Id(99L, 1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> habitLogService.deleteLog(1L, 99L));
    }
}