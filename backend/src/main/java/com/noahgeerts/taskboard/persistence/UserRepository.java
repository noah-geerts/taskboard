package com.noahgeerts.taskboard.persistence;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.noahgeerts.taskboard.domain.User;

public interface UserRepository extends CrudRepository<User, Long>{
    
    public Optional<User> findByEmail(String email);
}
