package com.noahgeerts.taskboard.service;

import com.noahgeerts.taskboard.domain.Task;
import com.noahgeerts.taskboard.domain.TaskRequestDto;
import com.noahgeerts.taskboard.domain.TaskResponseDto;

public interface TaskService {
    public TaskResponseDto createTask(TaskRequestDto taskRequestDto);

    public Iterable<TaskResponseDto> getAllTasks();

    public TaskResponseDto updateTask(Long tid, TaskRequestDto taskRequestDto);

    public void deleteTask(Long tid);

}
