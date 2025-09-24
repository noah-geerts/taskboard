package com.noahgeerts.taskboard.presentation;

import java.nio.file.AccessDeniedException;

import javax.naming.NameNotFoundException;

import org.apache.catalina.connector.Response;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.noahgeerts.taskboard.domain.dto.AuthReponseDto;
import com.noahgeerts.taskboard.domain.dto.AuthRequestDto;
import com.noahgeerts.taskboard.domain.dto.RefreshRequestDto;
import com.noahgeerts.taskboard.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/signup")
    public ResponseEntity<Void> signUp(@RequestBody @Valid AuthRequestDto authRequestDto) {
        try {
            userService.signUp(authRequestDto);
        } catch (DataIntegrityViolationException e) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthReponseDto> logIn(@RequestBody @Valid AuthRequestDto authRequestDto) {
        AuthReponseDto jwtToken;
        try {
            jwtToken = userService.logIn(authRequestDto);
        } catch (NameNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (AccessDeniedException e) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<>(jwtToken, HttpStatus.OK);
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthReponseDto> refresh(@RequestBody @Valid RefreshRequestDto refreshRequestDto) {
        AuthReponseDto authResponse;
        try {
            authResponse = userService.refresh(refreshRequestDto);
        } catch (AccessDeniedException e) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<>(authResponse, HttpStatus.OK);
    }
}
