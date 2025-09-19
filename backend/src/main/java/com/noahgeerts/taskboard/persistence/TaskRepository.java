package com.noahgeerts.taskboard.persistence;

import com.noahgeerts.taskboard.domain.Task;

import org.springframework.data.repository.CrudRepository;

public interface TaskRepository extends CrudRepository<Task, Long> {
}