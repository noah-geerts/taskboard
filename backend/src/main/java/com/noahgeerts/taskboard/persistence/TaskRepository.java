package com.noahgeerts.taskboard.persistence;

import com.noahgeerts.taskboard.domain.Task;
import com.noahgeerts.taskboard.domain.User;

import org.springframework.data.repository.CrudRepository;

public interface TaskRepository extends CrudRepository<Task, Long> {
    public Iterable<Task> findByUser(User user);
}