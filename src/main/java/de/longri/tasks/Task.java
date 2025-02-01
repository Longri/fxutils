package de.longri.tasks;


import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Represents a task with properties such as title, description, priority, status, and more.
 * A task can optionally have subtasks, dependencies, and associated metadata, such as tags and category.
 */
public class Task {


    /**
     * Enumeration representing task priority levels.
     */
    public enum Priority {
        NONE(0),        // No priority
        VERY_LOW(1),    // Very low priority
        LOW(2),         // Low priority
        MEDIUM(3),      // Medium priority
        HIGH(4),        // High priority
        VERY_HIGH(5);   // Very high priority

        private final int value;

        // Constructor to assign integer values
        Priority(int value) {
            this.value = value;
        }

        // Getter to retrieve the value
        public int getValue() {
            return value;
        }

        /**
         * Compares two priorities based on their numeric value.
         *
         * @param p1 First priority to compare.
         * @param p2 Second priority to compare.
         * @return A negative integer, zero, or a positive integer if the first priority
         * is less than, equal to, or greater than the second priority.
         */
        public static int comparePriorities( Priority p1, Priority p2) {
            return Integer.compare(p1.getValue(), p2.getValue());
        }
    }

    /**
     * Enumeration representing the various possible statuses of a task.
     */
    public enum Status {
        OPEN,           // Task is open
        IN_PROGRESS,    // Task is being worked on
        COMPLETED,      // Task has been completed
        BLOCKED,        // Task is blocked
        CANCELLED,      // Task has been cancelled
        ON_HOLD,        // Task is on hold
        NEW;            // Task is new
    }


    /**
     * Unique identifier for the task.
     */
    private int id;
    /**
     * Title or name of the task.
     */
    private String title;
    /**
     * Detailed description of the task.
     */
    private String description;
    /**
     * Priority level of the task using the {@link Priority} enum.
     */
    private Priority priority; // Usage of the Priority enum
    /**
     * Current status of the task using the {@link Status} enum.
     */
    private Status status;     // Usage of the Status enum

    /**
     * Deadline for task completion.
     */
    private LocalDateTime dueDate;

    /**
     * Duration (time interval) between two moments (optional).
     * This property can be used to store the duration directly,
     * if it is needed in a persistent form instead of being calculated on-demand.
     */
    private Duration duration;
    /**
     * Timestamp when the task was created.
     */
    private LocalDateTime creationDate;
    /**
     * Name of the individual assigned to the task.
     */
    private String assignee;
    /**
     * List of tags associated with the task for categorization or metadata.
     */
    private List<String> tags;
    /**
     * Indicates whether the task has been completed.
     */
    private boolean completed;
    /**
     * List of subtasks that are part of this task.
     */
    private List<Task> subtasks;
    /**
     * List of tasks that this task depends on for completion.
     */
    private List<Task> dependencies;
    /**
     * Category or group to which the task belongs.
     */
    private String category;

    /**
     * Constructs a new Task with the specified details.
     *
     * @param id          Unique identifier for the task.
     * @param title       Title or name of the task.
     * @param description Detailed description of the task.
     * @param priority    Priority level of the task (default is {@link Priority#NONE} if null).
     * @param status      Current status of the task (default is {@link Status#OPEN} if null).
     */
    public Task(int id, String title, String description, Priority priority, Status status) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.priority = priority == null ? Priority.NONE : priority; // Standardwert NONE
        this.status = status == null ? Status.OPEN : status;         // Standardwert OPEN
        this.creationDate = LocalDateTime.now();
        this.completed = false;
    }


    /**
     * Retrieves the unique identifier of the task.
     *
     * @return The unique identifier of the task.
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the unique identifier of the task.
     *
     * @param id The unique identifier to set.
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Retrieves the title of the task.
     *
     * @return The title of the task.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the title of the task.
     *
     * @param title The title to set.
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Retrieves the detailed description of the task.
     *
     * @return The description of the task.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the detailed description of the task.
     *
     * @param description The description to set.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Retrieves the priority of the task.
     *
     * @return The priority of the task.
     */
    public Priority getPriority() {
        return priority;
    }

    /**
     * Sets the priority of the task.
     *
     * @param priority The priority to set.
     */
    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    /**
     * Retrieves the current status of the task.
     *
     * @return The status of the task.
     */
    public Status getStatus() {
        return status;
    }

    /**
     * Sets the current status of the task.
     *
     * @param status The status to set.
     */
    public void setStatus(Status status) {
        this.status = status;
    }

    /**
     * Retrieves the due date of the task.
     *
     * @return The due date of the task.
     */
    public LocalDateTime getDueDate() {
        return dueDate;
    }

    /**
     * Sets the due date of the task.
     *
     * @param dueDate The due date to set.
     */
    public void setDueDate(LocalDateTime dueDate) {
        this.dueDate = dueDate;
    }

    /**
     * Retrieves the creation date of the task.
     *
     * @return The creation date of the task.
     */
    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    /**
     * Sets the creation date of the task.
     *
     * @param creationDate The creation date to set.
     */
    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    /**
     * Retrieves the name of the individual assigned to the task.
     *
     * @return The assignee of the task.
     */
    public String getAssignee() {
        return assignee;
    }

    /**
     * Sets the name of the individual assigned to the task.
     *
     * @param assignee The assignee to set.
     */
    public void setAssignee(String assignee) {
        this.assignee = assignee;
    }

    /**
     * Retrieves the list of tags associated with the task.
     *
     * @return The list of tags for the task.
     */
    public List<String> getTags() {
        return tags;
    }

    /**
     * Sets the list of tags associated with the task.
     *
     * @param tags The list of tags to set.
     */
    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    /**
     * Checks whether the task is completed.
     *
     * @return True if the task is completed, otherwise false.
     */
    public boolean isCompleted() {
        return completed;
    }

    /**
     * Sets the completion status of the task.
     *
     * @param completed True to mark the task as completed, otherwise false.
     */
    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    /**
     * Retrieves the list of subtasks within the task.
     *
     * @return The list of subtasks.
     */
    public List<Task> getSubtasks() {
        return subtasks;
    }

    /**
     * Sets the list of subtasks within the task.
     *
     * @param subtasks The list of subtasks to set.
     */
    public void setSubtasks(List<Task> subtasks) {
        this.subtasks = subtasks;
    }

    /**
     * Retrieves the list of tasks that this task depends on for completion.
     *
     * @return The list of dependencies.
     */
    public List<Task> getDependencies() {
        return dependencies;
    }

    /**
     * Sets the list of dependencies for the task.
     *
     * @param dependencies The list of dependencies to set.
     */
    public void setDependencies(List<Task> dependencies) {
        this.dependencies = dependencies;
    }

    /**
     * Retrieves the category or group associated with the task.
     *
     * @return The category of the task.
     */
    public String getCategory() {
        return category;
    }

    /**
     * Sets the category or group associated with the task.
     *
     * @param category The category to set.
     */
    public void setCategory(String category) {
        this.category = category;
    }

    /**
     * Retrieves the stored duration.
     *
     * @return The stored `Duration` instance or null if none has been set.
     */
    public Duration getDuration() {
        return duration;
    }

    /**
     * Sets the duration of the task.
     *
     * This can represent the time span between creation and the deadline
     * or some other specified duration (e.g., estimated time required to complete the task).
     *
     * @param duration A `Duration` instance representing the time span.
     */
    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    /**
     * Calculates a new `LocalDateTime` by adding the `duration` to the `dueDate`.
     *
     * @return The calculated `LocalDateTime`, or null if `dueDate` or `duration` is not set.
     */
    public LocalDateTime getStartDateTime() {
        if (dueDate != null && duration != null) {
            return dueDate.plus(duration); // Adds the duration to the due date
        }
        return null; // Return null if one of the values is not set
    }

    /**
     * Calculates and sets the `duration` property given a start `LocalDateTime` and the `dueDate`.
     *
     * @param start The starting date and time.
     */
    public void setStart(LocalDateTime start) {

        if (start != null && dueDate != null) {
            if(this.dueDate.isBefore(start)) throw new IllegalArgumentException("Start date must not be after due date");
            this.duration = Duration.between(start, dueDate); // Calculates the duration between start and dueDate
        } else {
            throw new IllegalArgumentException("Start date and due date must not be null");
        }
    }

}
