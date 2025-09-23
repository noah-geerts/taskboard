package com.noahgeerts.taskboard.persistence;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;

import com.noahgeerts.taskboard.domain.Task;
import com.noahgeerts.taskboard.domain.User;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
public class TaskRepositoryIntegrationTests {

    private TaskRepository taskRepository;
    private UserRepository userRepository;

    @Autowired
    TaskRepositoryIntegrationTests(TaskRepository taskRepository, UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    @Test
    public void testTaskRepositoryCanCreateAndFindTask() {

        // Arrange
        Task task = new Task();
        task.setTitle("Title");
        task.setDescription("cool description");
        task.setPriority((byte) 0);
        task.setStatus((byte) 0);

        User user = User.builder().email("f").password("a").build();
        userRepository.save(user);
        task.setUser(user);

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

        List<User> users = List.of(User.builder().email("a").password("a").build(),
                User.builder().email("b").password("a").build(), User.builder().email("c").password("a").build());
        userRepository.saveAll(users);
        t1.setUser(users.get(0));
        t2.setUser(users.get(1));
        t3.setUser(users.get(2));

        // Act
        taskRepository.saveAll(List.of(t1, t2, t3));
        Iterable<Task> result = taskRepository.findAll();

        // Assert
        assertThat(result).hasSize(3);

    }

}
