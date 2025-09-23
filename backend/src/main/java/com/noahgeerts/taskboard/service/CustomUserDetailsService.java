package com.noahgeerts.taskboard.service;

import java.util.Optional;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.noahgeerts.taskboard.domain.User;
import com.noahgeerts.taskboard.persistence.UserRepository;
import org.springframework.security.core.userdetails.UserDetailsService;


@Service
public class CustomUserDetailsService implements UserDetailsService {

    private UserRepository userRepository;
    
    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByEmail(username);
        if (user.isEmpty()) {
            throw new UsernameNotFoundException("No user exists with email " + username);
        }
        return user.get();
    }

}