package com.noahgeerts.taskboard.presentation;

import java.util.NoSuchElementException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.noahgeerts.taskboard.domain.Task;
import com.noahgeerts.taskboard.domain.TaskRequestDto;
import com.noahgeerts.taskboard.domain.TaskResponseDto;
import com.noahgeerts.taskboard.service.TaskService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

@RestController
public class TaskController {

    private TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping("/tasks")
    public ResponseEntity<TaskResponseDto> createBook(@RequestBody @Valid TaskRequestDto taskRequestDto) {
        return new ResponseEntity<>(taskService.createTask(taskRequestDto), HttpStatus.CREATED);
    }

    @GetMapping("/tasks")
    public ResponseEntity<Iterable<TaskResponseDto>> getAllTasks() {
        return new ResponseEntity<>(taskService.getAllTasks(), HttpStatus.OK);
    }

    @PatchMapping("/tasks")
    public ResponseEntity<Void> updateTask(@RequestParam @NotNull Long tid,
            @RequestBody @Valid TaskRequestDto taskRequestDto) {
        try {
            taskService.updateTask(tid, taskRequestDto);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/tasks")
    public ResponseEntity<Void> deleteTask(@RequestParam @NotNull Long tid) {
        try {
            taskService.deleteTask(tid);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/")
    public ResponseEntity<String> health() {
        return new ResponseEntity<>("Server up", HttpStatus.OK);
    }
}
