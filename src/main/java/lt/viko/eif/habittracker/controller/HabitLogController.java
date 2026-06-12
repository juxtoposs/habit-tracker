package lt.viko.eif.habittracker.controller;

import lt.viko.eif.habittracker.config.CacheSettings;
import lt.viko.eif.habittracker.model.HabitLog;
import lt.viko.eif.habittracker.service.HabitLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

/**
 * REST controller for managing habit completion logs.
 * Supports HATEOAS for Richardson Level 4 compliance.
 */
@RestController
@RequestMapping("/api/habits/{habitId}/logs")
@Tag(name = "Habit Logs", description = "API for habit completion records")
public class HabitLogController {

    private final HabitLogService habitLogService;

    /**
     * Constructor with dependency injection.
     *
     * @param habitLogService habit log service
     */
    public HabitLogController(HabitLogService habitLogService) {
        this.habitLogService = habitLogService;
    }

    /**
     * Get all completion logs for a specific habit.
     *
     * @param habitId habit ID
     * @return collection of logs with HATEOAS links
     * @throws lt.viko.eif.habittracker.exception.ResourceNotFoundException if habit is not found
     */
    @GetMapping
    @Operation(summary = "Get completion logs for a habit")
    public ResponseEntity<CollectionModel<HabitLogModel>> getLogsForHabit(@PathVariable Long habitId) {
        List<HabitLog> logs = habitLogService.findByHabitId(habitId);
        List<HabitLogModel> models = logs.stream()
                .map(this::toModel)
                .collect(Collectors.toList());

        CollectionModel<HabitLogModel> collection = CollectionModel.of(
                models,
                linkTo(methodOn(HabitLogController.class).getLogsForHabit(habitId)).withSelfRel(),
                linkTo(methodOn(HabitController.class).getHabitById(habitId)).withRel("habit"),
                linkTo(methodOn(HabitLogController.class).getLogsForHabit(habitId)).withRel("mark-completed")
        );

        return ResponseEntity.ok()
                .cacheControl(CacheSettings.shortPrivateCache())
                .body(collection);
    }

    /**
     * Mark a habit as completed on a specified date.
     *
     * @param habitId       habit ID
     * @param completedDate completion date (format yyyy-MM-dd)
     * @return created log with status 201
     * @throws lt.viko.eif.habittracker.exception.ResourceNotFoundException  if habit is not found
     * @throws lt.viko.eif.habittracker.exception.DuplicateResourceException if log already exists for this date
     */
    @PostMapping
    @Operation(summary = "Mark habit as completed")
    public ResponseEntity<HabitLogModel> markCompleted(
            @PathVariable Long habitId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate completedDate) {
        HabitLog log = habitLogService.markCompleted(habitId, completedDate);
        HabitLogModel model = toModel(log);
        return ResponseEntity.status(HttpStatus.CREATED).body(model);
    }

    /**
     * Delete a completion record.
     *
     * @param habitId habit ID (for correct URL structure)
     * @param logId   log ID
     * @return status 204 No Content
     * @throws lt.viko.eif.habittracker.exception.ResourceNotFoundException if log is not found
     */
    @DeleteMapping("/{logId}")
    @Operation(summary = "Delete a completion record")
    public ResponseEntity<Void> deleteLog(@PathVariable Long habitId, @PathVariable Long logId) {
        habitLogService.deleteLog(habitId, logId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Converts a HabitLog entity to a HATEOAS model.
     *
     * @param log log entity
     * @return model with links
     */
    private HabitLogModel toModel(HabitLog log) {
        HabitLogModel model = new HabitLogModel(log);

        if (log.getHabit() == null) {
            return model;
        }

        Long habitId = log.getHabit().getId();

        model.add(linkTo(methodOn(HabitLogController.class)
                .getLogById(habitId, log.getId())).withSelfRel());

        model.add(linkTo(methodOn(HabitLogController.class)
                .getLogsForHabit(habitId)).withRel("logs"));

        model.add(linkTo(methodOn(HabitController.class)
                .getHabitById(habitId)).withRel("habit"));

        model.add(linkTo(methodOn(HabitLogController.class)
                .deleteLog(habitId, log.getId())).withRel("delete"));

        return model;
    }

    /**
     * Get a single completion log for a habit.
     *
     * @param habitId habit ID
     * @param logId log ID
     * @return completion log with HATEOAS links
     * @throws lt.viko.eif.habittracker.exception.ResourceNotFoundException if habit or log is not found
     */
    @GetMapping("/{logId}")
    @Operation(summary = "Get completion log by ID")
    public ResponseEntity<HabitLogModel> getLogById(
            @PathVariable Long habitId,
            @PathVariable Long logId
    ) {
        HabitLog log = habitLogService.findByIdAndHabitId(habitId, logId);
        HabitLogModel model = toModel(log);

        return ResponseEntity.ok()
                .cacheControl(CacheSettings.shortPrivateCache())
                .body(model);
    }
}
