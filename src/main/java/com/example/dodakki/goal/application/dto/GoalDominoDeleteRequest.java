package com.example.dodakki.goal.application.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GoalDominoDeleteRequest {
    @NotNull
    private Long goalId; // 삭제할 목표 ID
}
