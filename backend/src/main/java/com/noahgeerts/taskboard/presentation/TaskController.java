package com.noahgeerts.taskboard.presentation;

import java.nio.file.AccessDeniedException;
import java.util.NoSuchElementException;

import org.springframework.boot.autoconfigure.graphql.GraphQlProperties.Http;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.config.annotation.rsocket.RSocketSecurity.AuthorizePayloadsSpec.Access;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.noahgeerts.taskboard.domain.User;
import com.noahgeerts.taskboard.domain.dto.TaskRequestDto;
import com.noahgeerts.taskboard.domain.dto.TaskResponseDto;
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
    public ResponseEntity<TaskResponseDto> createTask(@RequestBody @Valid TaskRequestDto taskRequestDto, @AuthenticationPrincipal User user) {
        return new ResponseEntity<>(taskService.createTask(taskRequestDto, user), HttpStatus.CREATED);
    }

    @GetMapping("/tasks")
    public ResponseEntity<Iterable<TaskResponseDto>> getAllTasks(@AuthenticationPrincipal User user) {
        return new ResponseEntity<>(taskService.getAllTasks(user), HttpStatus.OK);
    }

    @PatchMapping("/tasks")
    public ResponseEntity<Void> updateTask(@RequestParam @NotNull Long tid,
            @RequestBody @Valid TaskRequestDto taskRequestDto, @AuthenticationPrincipal User user) {
        try {
            taskService.updateTask(tid, taskRequestDto, user);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (AccessDeniedException e) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/tasks")
    public ResponseEntity<Void> deleteTask(@RequestParam @NotNull Long tid, @AuthenticationPrincipal User user) {
        try {
            taskService.deleteTask(tid, user);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (AccessDeniedException e) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/")
    public ResponseEntity<String> health() {
        return new ResponseEntity<>("Server up", HttpStatus.OK);
    }
}
