package de.longri.tasks;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test class for the {@link Task} class.
 * This class includes tests for all public methods, functionality for subtasks, dependencies,
 * and sorting tasks by priority.
 */
class TaskTest {

    private Task task;

    /**
     * Sets up default task object before each test runs.
     * Initializes a task with the following default values:
     * - id: 1
     * - title: "Test Task"
     * - description: "This is a test task"
     * - priority: MEDIUM
     * - status: OPEN
     */
    @BeforeEach
    void setUp() {
        task = new Task(1, "Test Task", "This is a test task", Task.Priority.MEDIUM, Task.Status.OPEN);
    }

    /**
     * Tests the retrieval and updating of the task ID.
     * Ensures that the ID is set properly and can be retrieved correctly.
     */
    @Test
    void testGetAndSetId() {
        assertEquals(1, task.getId());
        task.setId(2);
        assertEquals(2, task.getId());
    }

    /**
     * Tests the retrieval and updating of the task title.
     */
    @Test
    void testGetAndSetTitle() {
        assertEquals("Test Task", task.getTitle());
        task.setTitle("Updated Task Title");
        assertEquals("Updated Task Title", task.getTitle());
    }

    /**
     * Tests the retrieval and updating of the task description.
     */
    @Test
    void testGetAndSetDescription() {
        assertEquals("This is a test task", task.getDescription());
        task.setDescription("Updated description");
        assertEquals("Updated description", task.getDescription());
    }

    /**
     * Tests the retrieval and updating of the task priority.
     * Ensures that the priority enum values can be retrieved and updated correctly.
     */
    @Test
    void testGetAndSetPriority() {
        assertEquals(Task.Priority.MEDIUM, task.getPriority());
        task.setPriority(Task.Priority.HIGH);
        assertEquals(Task.Priority.HIGH, task.getPriority());
    }

    /**
     * Tests the retrieval and updating of the task status.
     * Ensures status transitions are reflected accurately.
     */
    @Test
    void testGetAndSetStatus() {
        assertEquals(Task.Status.OPEN, task.getStatus());
        task.setStatus(Task.Status.IN_PROGRESS);
        assertEquals(Task.Status.IN_PROGRESS, task.getStatus());
    }

    /**
     * Tests the retrieval and updating of the task due date.
     * Ensures that a due date can be set and retrieved correctly.
     */
    @Test
    void testGetAndSetDueDate() {
        assertNull(task.getDueDate());
        LocalDateTime dueDate = LocalDateTime.now().plusDays(5);
        task.setDueDate(dueDate);
        assertEquals(dueDate, task.getDueDate());
    }

    /**
     * Tests the retrieval and setting of the creation date.
     */
    @Test
    void testGetAndSetCreationDate() {
        assertNotNull(task.getCreationDate()); // Initial creation date should not be null
        LocalDateTime creationDate = LocalDateTime.now().minusDays(2);
        task.setCreationDate(creationDate);
        assertEquals(creationDate, task.getCreationDate());
    }

    /**
     * Tests the retrieval and setting of the assignee.
     */
    @Test
    void testGetAndSetAssignee() {
        assertNull(task.getAssignee());
        task.setAssignee("John Doe");
        assertEquals("John Doe", task.getAssignee());
    }

    /**
     * Tests the retrieval and setting of tags.
     * Ensures that tags are properly stored and retrieved.
     */
    @Test
    void testGetAndSetTags() {
        assertNull(task.getTags());
        List<String> tags = Arrays.asList("tag1", "tag2", "tag3");
        task.setTags(tags);
        assertEquals(tags, task.getTags());
    }

    /**
     * Tests the retrieval and updating of the task completion status.
     */
    @Test
    void testIsAndSetCompleted() {
        assertFalse(task.isCompleted());
        task.setCompleted(true);
        assertTrue(task.isCompleted());
    }

    /**
     * Tests the retrieval and setting of subtasks.
     * Verifies that subtasks can be added and retrieved properly.
     */
    @Test
    void testGetAndSetSubtasks() {
        assertNull(task.getSubtasks());
        Task subtask1 = new Task(2, "Subtask 1", "Description 1", Task.Priority.LOW, Task.Status.NEW);
        Task subtask2 = new Task(3, "Subtask 2", "Description 2", Task.Priority.HIGH, Task.Status.NEW);
        List<Task> subtasks = Arrays.asList(subtask1, subtask2);
        task.setSubtasks(subtasks);
        assertEquals(subtasks, task.getSubtasks());
    }

    /**
     * Tests the retrieval and setting of dependencies.
     */
    @Test
    void testGetAndSetDependencies() {
        assertNull(task.getDependencies());
        Task dependency1 = new Task(4, "Dependency 1", "Description 1", Task.Priority.HIGH, Task.Status.COMPLETED);
        Task dependency2 = new Task(5, "Dependency 2", "Description 2", Task.Priority.MEDIUM, Task.Status.IN_PROGRESS);
        List<Task> dependencies = Arrays.asList(dependency1, dependency2);
        task.setDependencies(dependencies);
        assertEquals(dependencies, task.getDependencies());
    }

    /**
     * Tests the retrieval and setting of the task category.
     */
    @Test
    void testGetAndSetCategory() {
        assertNull(task.getCategory());
        task.setCategory("Software");
        assertEquals("Software", task.getCategory());
    }

    /**
     * Tests the functionality of subtasks.
     * Ensures that subtasks are properly added and their properties match.
     */
    @Test
    void testSubtasksFunctionality() {
        Task subtask1 = new Task(2, "Subtask 1", "Description 1", Task.Priority.MEDIUM, Task.Status.NEW);
        Task subtask2 = new Task(3, "Subtask 2", "Description 2", Task.Priority.HIGH, Task.Status.IN_PROGRESS);

        task.setSubtasks(Arrays.asList(subtask1, subtask2));

        List<Task> subtasks = task.getSubtasks();
        assertEquals(2, subtasks.size());
        assertEquals("Subtask 1", subtasks.get(0).getTitle());
        assertEquals(Task.Priority.HIGH, subtasks.get(1).getPriority());
    }

    /**
     * Tests the functionality of task dependencies.
     * Ensures that dependencies can be set and their properties verified.
     */
    @Test
    void testDependenciesFunctionality() {
        Task dependency1 = new Task(4, "Dependency 1", "Description 1", Task.Priority.HIGH, Task.Status.COMPLETED);
        Task dependency2 = new Task(5, "Dependency 2", "Description 2", Task.Priority.LOW, Task.Status.IN_PROGRESS);

        task.setDependencies(Arrays.asList(dependency1, dependency2));

        List<Task> dependencies = task.getDependencies();
        assertEquals(2, dependencies.size());
        assertEquals("Dependency 1", dependencies.get(0).getTitle());
        assertEquals(Task.Status.IN_PROGRESS, dependencies.get(1).getStatus());
    }

    /**
     * Tests the sorting of tasks based on priority.
     * Simulates a scenario where tasks with different priorities are sorted.
     */
    @Test
    void testPrioritySorting() {
        Task task1 = new Task(6, "Task 1", "Description", Task.Priority.MEDIUM, Task.Status.NEW);
        Task task2 = new Task(7, "Task 2", "Description", Task.Priority.HIGH, Task.Status.NEW);
        Task task3 = new Task(8, "Task 3", "Description", Task.Priority.LOW, Task.Status.NEW);

        List<Task> tasks = new ArrayList<>(Arrays.asList(task1, task2, task3));

        // Sorting tasks by priority in descending order using Priority.comparePriorities()
        tasks.sort((t1, t2) -> Task.Priority.comparePriorities(t2.getPriority(), t1.getPriority()));

        assertEquals(Task.Priority.HIGH, tasks.get(0).getPriority());
        assertEquals(Task.Priority.MEDIUM, tasks.get(1).getPriority());
        assertEquals(Task.Priority.LOW, tasks.get(2).getPriority());
    }

    /**
     * Test getStartDateTime() with valid dueDate and duration.
     * Ensures that the method correctly calculates the resulting start date.
     */
    @Test
    void testGetStartDateTime_ValidDueDateAndDuration() {
        LocalDateTime dueDate = LocalDateTime.of(2023, 12, 25, 12, 0); // Set due date
        Duration duration = Duration.ofHours(5); // Set duration

        task.setDueDate(dueDate);               // Assign dueDate to the task
        task.setDuration(duration);             // Assign duration to the task

        LocalDateTime result = task.getStartDateTime();   // Calculate start date
        assertNotNull(result);                            // Result should not be null
        assertEquals(dueDate.plus(duration), result);     // Assert result matches dueDate + duration
    }

    /**
     * Test getStartDateTime() when dueDate is not set.
     * Verifies that the method returns null when dueDate is missing.
     */
    @Test
    void testGetStartDateTime_NoDueDate() {
        Duration duration = Duration.ofHours(5); // Assign only the duration
        task.setDuration(duration);

        LocalDateTime result = task.getStartDateTime(); // Attempt to calculate start date without dueDate
        assertNull(result);                             // Result should be null
    }

    /**
     * Test getStartDateTime() when duration is not set.
     * Verifies that the method returns null when duration is not assigned.
     */
    @Test
    void testGetStartDateTime_NoDuration() {
        LocalDateTime dueDate = LocalDateTime.of(2023, 12, 25, 12, 0); // Assign dueDate
        task.setDueDate(dueDate);

        LocalDateTime result = task.getStartDateTime(); // Attempt to calculate start date without duration
        assertNull(result);                             // Result should be null
    }

    /**
     * Test getStartDateTime() when both dueDate and duration are missing.
     * Ensures that the method returns null when all required properties are not set.
     */
    @Test
    void testGetStartDateTime_NoDueDateAndDuration() {
        LocalDateTime result = task.getStartDateTime(); // Call with no values set
        assertNull(result);                             // Result should be null
    }

    /**
     * Test setStart() with valid start and dueDate values.
     * Ensures that the duration is correctly calculated as the difference
     * between start and dueDate.
     */
    @Test
    void testSetStart_ValidStartAndDueDate() {
        LocalDateTime start = LocalDateTime.of(2023, 12, 24, 8, 0);  // Define start date
        LocalDateTime dueDate = LocalDateTime.of(2023, 12, 25, 12, 0); // Define due date

        task.setDueDate(dueDate);      // Assign dueDate to the task
        task.setStart(start);          // Calculate and set duration

        assertNotNull(task.getDuration());                       // Duration should not be null
        assertEquals(Duration.ofHours(28), task.getDuration());  // Duration should be 28 hours
    }

    /**
     * Test setStart() when start date is null.
     * Ensures that an exception is thrown when the start date is not provided.
     */
    @Test
    void testSetStart_NullStart() {
        LocalDateTime dueDate = LocalDateTime.of(2023, 12, 25, 12, 0); // Set only dueDate

        task.setDueDate(dueDate); // Assign dueDate to the task

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            task.setStart(null); // Attempt with null start date
        });
        assertEquals("Start date and due date must not be null", exception.getMessage()); // Validate exception message
    }

    /**
     * Test setStart() when dueDate is missing.
     * Ensures that an exception is thrown when the dueDate property is not set.
     */
    @Test
    void testSetStart_NullDueDate() {
        LocalDateTime start = LocalDateTime.of(2023, 12, 24, 8, 0); // Define start date

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            task.setStart(start); // Attempt to calculate duration with no dueDate
        });
        assertEquals("Start date and due date must not be null", exception.getMessage()); // Validate exception message
    }

    /**
     * Test setStart() when both start and dueDate are null.
     * Ensures that an exception is thrown when no values are provided.
     */
    @Test
    void testSetStart_NullStartAndDueDate() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            task.setStart(null); // Attempt with null values
        });
        assertEquals("Start date and due date must not be null", exception.getMessage()); // Validate exception message
    }

    /**
     * Test setStart() when start date is after dueDate.
     * Ensures that an exception is thrown when start > dueDate, as this results in a negative Duration.
     */
    @Test
    void testSetStart_DurationIsNegative() {
        LocalDateTime start = LocalDateTime.of(2023, 12, 26, 12, 0); // Define start after dueDate
        LocalDateTime dueDate = LocalDateTime.of(2023, 12, 25, 12, 0); // Define dueDate

        task.setDueDate(dueDate); // Assign dueDate to the task

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            task.setStart(start); // Attempt with invalid dates
        });
        assertEquals("Start date must not be after due date", exception.getMessage()); // Validate exception message
    }


}