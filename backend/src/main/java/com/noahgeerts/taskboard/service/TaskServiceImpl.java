package com.noahgeerts.taskboard.service;

import java.nio.file.AccessDeniedException;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.StreamSupport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.noahgeerts.taskboard.domain.Task;
import com.noahgeerts.taskboard.domain.User;
import com.noahgeerts.taskboard.domain.dto.TaskRequestDto;
import com.noahgeerts.taskboard.domain.dto.TaskResponseDto;
import com.noahgeerts.taskboard.mapper.Mapper;
import com.noahgeerts.taskboard.persistence.TaskRepository;

@Service
public class TaskServiceImpl implements TaskService {
    private TaskRepository taskRepo;
    private static final Logger log = LoggerFactory.getLogger(TaskServiceImpl.class);

    private final Mapper mapper;

    // DI (taskRepo bean is automatically created in Spring Context because of
    // CrudRepository Interface)
    public TaskServiceImpl(TaskRepository taskRepo, Mapper mapper) {
        this.taskRepo = taskRepo;
        this.mapper = mapper;
    }

    public TaskResponseDto createTask(TaskRequestDto taskRequestDto, User user) {
        Task newTask = mapper.map(taskRequestDto, Task.class);
        newTask.setUser(user);
        Task result = taskRepo.save(newTask);
        return mapper.map(result, TaskResponseDto.class);
    }

    public Iterable<TaskResponseDto> getAllTasks(User user) {
        Iterable<Task> result = taskRepo.findByUser(user);

        return StreamSupport.stream(result.spliterator(), false)
                .map(task -> mapper.map(task, TaskResponseDto.class))
                .toList();
    }

    public TaskResponseDto updateTask(Long tid, TaskRequestDto taskRequestDto, User user) throws AccessDeniedException {
        log.info(user.getEmail());
        Optional<Task> current = taskRepo.findById(tid);
        if (current.isEmpty())
            throw new NoSuchElementException();
        Task task = current.get();
        if (!task.getUser().getUid().equals(user.getUid()))
            throw new AccessDeniedException("This user does not own this task");

        Task newTask = mapper.map(taskRequestDto, Task.class);
        newTask.setTid(tid);
        newTask.setUser(user);
        Task result = taskRepo.save(newTask);
        return mapper.map(result, TaskResponseDto.class);
    }

    public void deleteTask(Long tid, User user) throws AccessDeniedException {
        log.info(user.getEmail());
        Optional<Task> current = taskRepo.findById(tid);
        if (current.isEmpty())
            throw new NoSuchElementException();
        Task task = current.get();
        if (!task.getUser().getUid().equals(user.getUid()))
            throw new AccessDeniedException("This user does not own this task");

        Task toDelete = new Task();
        toDelete.setTid(tid);
        taskRepo.delete(toDelete);
    }

}
