package com.noahgeerts.taskboard.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskResponseDto {
    private Long tid;

    private String title;
    private String description;

    private byte status;
    private byte priority;
}
