package com.noahgeerts.taskboard.service;

import java.nio.file.AccessDeniedException;

import com.noahgeerts.taskboard.domain.Task;
import com.noahgeerts.taskboard.domain.User;
import com.noahgeerts.taskboard.domain.dto.TaskRequestDto;
import com.noahgeerts.taskboard.domain.dto.TaskResponseDto;

public interface TaskService {
    public TaskResponseDto createTask(TaskRequestDto taskRequestDto, User user);

    public Iterable<TaskResponseDto> getAllTasks(User user);

    public TaskResponseDto updateTask(Long tid, TaskRequestDto taskRequestDto, User user) throws AccessDeniedException;

    public void deleteTask(Long tid, User user) throws AccessDeniedException;

}
