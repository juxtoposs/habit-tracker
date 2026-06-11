package lt.viko.eif.habittracker.controller;

import lt.viko.eif.habittracker.exception.ResourceNotFoundException;
import lt.viko.eif.habittracker.model.Habit;
import lt.viko.eif.habittracker.service.HabitService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

/**
 * REST controller for managing habits.
 * Implements Richardson Maturity Model Level 4 via HATEOAS.
 */
@RestController
@RequestMapping("/api/habits")
@Tag(name = "Habits", description = "API for managing habits")
public class HabitController {

    private final HabitService habitService;

    /**
     * Constructor with dependency injection.
     *
     * @param habitService habit service
     */
    public HabitController(HabitService habitService) {
        this.habitService = habitService;
    }

    /**
     * Get a list of all habits.
     *
     * @return collection of habits with HATEOAS links
     */
    @GetMapping
    @Operation(summary = "Get all habits")
    public ResponseEntity<CollectionModel<HabitModel>> getAllHabits() {
        List<Habit> habits = habitService.findAll();
        List<HabitModel> models = habits.stream()
                .map(this::toModel)
                .collect(Collectors.toList());

        Link selfLink = linkTo(methodOn(HabitController.class).getAllHabits()).withSelfRel();
        CollectionModel<HabitModel> collection = CollectionModel.of(models, selfLink);
        return ResponseEntity.ok(collection);
    }

    /**
     * Get a habit by ID.
     *
     * @param id habit identifier
     * @return habit with HATEOAS links
     * @throws lt.viko.eif.habittracker.exception.ResourceNotFoundException if habit is not found
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get habit by ID")
    public ResponseEntity<EntityModel<HabitModel>> getHabitById(@PathVariable Long id) {
        Habit habit = habitService.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Habit", id));
        HabitModel model = toModel(habit);
        return ResponseEntity.ok(EntityModel.of(model));
    }

    /**
     * Create a new habit.
     *
     * @param habit habit data
     * @return created habit with status 201
     */
    @PostMapping
    @Operation(summary = "Create a new habit")
    public ResponseEntity<EntityModel<HabitModel>> createHabit(@Valid @RequestBody Habit habit) {
        Habit created = habitService.create(habit);
        HabitModel model = toModel(created);
        return ResponseEntity.status(HttpStatus.CREATED).body(EntityModel.of(model));
    }

    /**
     * Update an existing habit.
     *
     * @param id    habit identifier
     * @param habit new data
     * @return updated habit
     * @throws lt.viko.eif.habittracker.exception.ResourceNotFoundException if habit is not found
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update a habit")
    public ResponseEntity<EntityModel<HabitModel>> updateHabit(
            @PathVariable Long id,
            @Valid @RequestBody Habit habit) {
        Habit updated = habitService.update(id, habit);
        HabitModel model = toModel(updated);
        return ResponseEntity.ok(EntityModel.of(model));
    }

    /**
     * Delete a habit.
     *
     * @param id habit identifier
     * @return status 204 No Content
     * @throws lt.viko.eif.habittracker.exception.ResourceNotFoundException if habit is not found
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a habit")
    public ResponseEntity<Void> deleteHabit(@PathVariable Long id) {
        habitService.delete(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Converts a Habit entity to a HATEOAS model and adds links.
     *
     * @param habit habit entity
     * @return model with links
     */
    private HabitModel toModel(Habit habit) {
        HabitModel model = new HabitModel(habit);
        model.add(linkTo(methodOn(HabitController.class).getHabitById(habit.getId())).withSelfRel());
        model.add(linkTo(methodOn(HabitLogController.class).getLogsForHabit(habit.getId())).withRel("logs"));
        model.add(linkTo(methodOn(HabitController.class).deleteHabit(habit.getId())).withRel("delete"));
        return model;
    }
}
