package com.noahgeerts.taskboard.domain.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthReponseDto {
    private String jwt;
    private String refreshToken;
}
