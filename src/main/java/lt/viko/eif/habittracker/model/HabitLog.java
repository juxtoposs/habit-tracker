package lt.viko.eif.habittracker.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * Entity representing a habit completion record for a specific date.
 * Linked to a habit via Many-to-One relationship.
 */
@Entity
@Table(name = "habit_logs")
public class HabitLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Completion date is required")
    @Column(name = "completed_date", nullable = false)
    private LocalDate completedDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "habit_id", nullable = false)
    @JsonIgnore
    private Habit habit;

    /**
     * Default constructor for JPA.
     */
    public HabitLog() {
    }

    /**
     * Parameterized constructor.
     *
     * @param completedDate date when the habit was completed
     */
    public HabitLog(LocalDate completedDate) {
        this.completedDate = completedDate;
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getCompletedDate() {
        return completedDate;
    }

    public void setCompletedDate(LocalDate completedDate) {
        this.completedDate = completedDate;
    }

    public Habit getHabit() {
        return habit;
    }

    public void setHabit(Habit habit) {
        this.habit = habit;
    }
}
