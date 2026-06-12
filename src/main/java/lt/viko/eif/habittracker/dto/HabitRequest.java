package lt.viko.eif.habittracker.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request DTO used when creating or updating a habit.
 * Separates API input data from the Habit JPA entity.
 */
public class HabitRequest {

    @NotBlank(message = "Habit name cannot be blank")
    @Size(max = 100, message = "Name must not exceed 100 characters")
    private String name;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;

    /**
     * Default constructor required for JSON deserialization.
     */
    public HabitRequest() {
    }

    /**
     * Creates a habit request with name and description.
     *
     * @param name habit name
     * @param description habit description
     */
    public HabitRequest(String name, String description) {
        this.name = name;
        this.description = description;
    }

    /**
     * Returns the habit name.
     *
     * @return habit name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the habit name.
     *
     * @param name habit name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the habit description.
     *
     * @return habit description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the habit description.
     *
     * @param description habit description
     */
    public void setDescription(String description) {
        this.description = description;
    }
}