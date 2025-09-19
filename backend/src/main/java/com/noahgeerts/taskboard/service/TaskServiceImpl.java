package com.noahgeerts.taskboard.service;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.StreamSupport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.noahgeerts.taskboard.domain.Task;
import com.noahgeerts.taskboard.domain.TaskRequestDto;
import com.noahgeerts.taskboard.domain.TaskResponseDto;
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

    public TaskResponseDto createTask(TaskRequestDto taskRequestDto) {
        Task newTask = mapper.map(taskRequestDto, Task.class);
        Task result = taskRepo.save(newTask);
        return mapper.map(result, TaskResponseDto.class);
    }

    public Iterable<TaskResponseDto> getAllTasks() {
        Iterable<Task> result = taskRepo.findAll();

        return StreamSupport.stream(result.spliterator(), false)
                .map(task -> mapper.map(task, TaskResponseDto.class))
                .toList();
    }

    public TaskResponseDto updateTask(Long tid, TaskRequestDto taskRequestDto) {
        Optional<Task> current = taskRepo.findById(tid);
        if (current.isEmpty())
            throw new NoSuchElementException();

        Task newTask = mapper.map(taskRequestDto, Task.class);
        newTask.setTid(tid);
        Task result = taskRepo.save(newTask);
        return mapper.map(result, TaskResponseDto.class);
    }

    public void deleteTask(Long tid) {
        Optional<Task> current = taskRepo.findById(tid);
        if (current.isEmpty())
            throw new NoSuchElementException();

        Task toDelete = new Task();
        toDelete.setTid(tid);
        taskRepo.delete(toDelete);
    }

}
