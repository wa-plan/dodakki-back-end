package com.example.dodakki.goal.application.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class GoalDominoDateUpdateRequest {
    @NotNull
    private Long goalId; // 수정하고자 하는 목표 ID

    @NotNull
    private LocalDate oldDate; // 기존 날짜

    @NotNull
    private LocalDate newDate; // 새로운 날짜
}
