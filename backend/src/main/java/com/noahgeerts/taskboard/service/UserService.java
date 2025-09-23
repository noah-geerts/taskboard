package com.noahgeerts.taskboard.service;

import java.nio.file.AccessDeniedException;
import java.util.Optional;

import javax.naming.NameNotFoundException;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;

import com.noahgeerts.taskboard.domain.User;
import com.noahgeerts.taskboard.domain.dto.AuthRequestDto;
import com.noahgeerts.taskboard.persistence.UserRepository;

@Service
public class UserService {
    
    private UserRepository userRepository;
    private JwtService jwtService;
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    
    public UserService(UserRepository userRepository, JwtService jwtService) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
    }

    public void signUp(AuthRequestDto signupRequest) {
        User newUser = new User();
        newUser.setEmail(signupRequest.getEmail());
        newUser.setPassword(signupRequest.getPassword());
        userRepository.save(newUser);
    }

    public String logIn(AuthRequestDto loginRequest) throws NameNotFoundException, AccessDeniedException {
        Optional<User> userOptional = userRepository.findByEmail(loginRequest.getEmail());
        if(userOptional.isEmpty())
            throw new NameNotFoundException();
        User user = userOptional.get();
        if(!user.getPassword().equals(loginRequest.getPassword()))
            throw new AccessDeniedException("Password incorrect");
        String jwtToken = jwtService.generateToken(user);
        return jwtToken;
    }

}
