package com.noahgeerts.taskboard.presentation;

import java.nio.file.AccessDeniedException;

import javax.naming.NameNotFoundException;

import org.apache.catalina.connector.Response;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import com.noahgeerts.taskboard.domain.dto.AuthInternalDto;
import com.noahgeerts.taskboard.domain.dto.AuthRequestDto;
import com.noahgeerts.taskboard.domain.dto.AuthResponseDto;
import com.noahgeerts.taskboard.mapper.Mapper;
import com.noahgeerts.taskboard.service.AuthService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    private AuthService userService;
    private Mapper mapper;

    public AuthController(AuthService userService, Mapper mapper) {
        this.userService = userService;
        this.mapper = mapper;
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
    public ResponseEntity<AuthResponseDto> logIn(@RequestBody @Valid AuthRequestDto authRequestDto) {
        AuthInternalDto internalAuthDto;
        try {
            internalAuthDto = userService.logIn(authRequestDto);
        } catch (NameNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (AccessDeniedException e) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", internalAuthDto.getRefreshToken())
                .httpOnly(true).secure(true).path("/auth").build();
        AuthResponseDto authResponse = mapper.map(internalAuthDto, AuthResponseDto.class);
        return ResponseEntity.ok().header("Set-Cookie", refreshCookie.toString()).body(authResponse);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logOut() {
        ResponseCookie logoutCookie = ResponseCookie.from("refreshToken", "")
                .httpOnly(true).secure(true).path("/auth").build();
        return ResponseEntity.ok().header("Set-Cookie", logoutCookie.toString()).build();
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponseDto> refresh(HttpServletRequest request) {
        // Extract refresh token from cookie
        String refreshToken = null;
        if (request.getCookies() != null) {
            for (Cookie c : request.getCookies()) {
                if ("refreshToken".equals(c.getName())) {
                    refreshToken = c.getValue();
                }
            }
        }

        if (refreshToken == null) {
            return ResponseEntity.badRequest().build();
        }

        // Validate refresh token and get new refresh token and JWT
        AuthInternalDto internalAuthDto;
        try {
            internalAuthDto = userService.refresh(refreshToken);
        } catch (AccessDeniedException e) {
            log.info(e.getMessage());
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        // Set refresh token as a cookie, return JWT
        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", internalAuthDto.getRefreshToken())
                .httpOnly(true).secure(true).path("/auth").build();
        AuthResponseDto authResponse = mapper.map(internalAuthDto, AuthResponseDto.class);
        return ResponseEntity.ok().header("Set-Cookie", refreshCookie.toString()).body(authResponse);
    }
}
