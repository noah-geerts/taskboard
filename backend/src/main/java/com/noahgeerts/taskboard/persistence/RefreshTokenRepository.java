package com.noahgeerts.taskboard.persistence;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.noahgeerts.taskboard.domain.RefreshToken;

public interface RefreshTokenRepository extends CrudRepository<RefreshToken, Long> {
    public Optional<RefreshToken> findByToken(String token);
}
