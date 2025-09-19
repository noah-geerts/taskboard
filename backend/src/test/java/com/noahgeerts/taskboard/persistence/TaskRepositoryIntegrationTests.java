package com.noahgeerts.taskboard.persistence;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;

import com.noahgeerts.taskboard.domain.Task;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
public class TaskRepositoryIntegrationTests {

    private TaskRepository taskRepository;

    @Autowired
    TaskRepositoryIntegrationTests(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Test
    public void testTaskRepositoryCanCreateAndFindTask() {

        // Arrange
        Task task = new Task();
        task.setTitle("Title");
        task.setDescription("cool description");
        task.setPriority((byte) 0);
        task.setStatus((byte) 0);

        // Act
        taskRepository.save(task);
        Optional<Task> result = taskRepository.findById(task.getTid());

        // Assert
        assertThat(result).isNotEmpty();
        assertThat(result.get()).isEqualTo(task);
    }

    @Test
    public void testTaskRepositoryCreatesCorrectNumberOfTasks() {
        // Arrange
        Task t1 = new Task();
        Task t2 = new Task();
        Task t3 = new Task();

        // Act
        taskRepository.saveAll(List.of(t1, t2, t3));
        Iterable<Task> result = taskRepository.findAll();

        // Assert
        assertThat(result).hasSize(3);

    }

}
