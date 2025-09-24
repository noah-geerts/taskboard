package com.noahgeerts.taskboard.domain.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class RefreshRequestDto {
    private String token;
}
