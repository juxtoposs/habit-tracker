package lt.viko.eif.habittracker.controller;

import lt.viko.eif.habittracker.model.HabitLog;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import java.time.LocalDate;

/**
 * HATEOAS model for representing a habit completion log.
 */
@Relation(value = "log", collectionRelation = "logs")
public class HabitLogModel extends RepresentationModel<HabitLogModel> {

    private Long id;
    private LocalDate completedDate;
    private Long habitId;

    /**
     * Default constructor.
     */
    public HabitLogModel() {
    }

    /**
     * Creates a model from a HabitLog entity.
     *
     * @param log log entity
     */
    public HabitLogModel(HabitLog log) {
        this.id = log.getId();
        this.completedDate = log.getCompletedDate();
        if (log.getHabit() != null) {
            this.habitId = log.getHabit().getId();
        }
    }

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

    public Long getHabitId() {
        return habitId;
    }

    public void setHabitId(Long habitId) {
        this.habitId = habitId;
    }
}
