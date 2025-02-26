package com.example.dodakki.goal.application.dto;

import com.example.dodakki.goal.domain.Repetition;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class GoalFullUpdateRequest {
    @NotNull
    private Long thirdGoalId; // 수정할 목표 ID

    @NotNull
    private String name; // 목표 이름 수정

    @NotNull
    private List<LocalDate> dates; // 새로운 날짜 리스트

    @NotNull
    private Repetition repetition; // 반복 주기 (EVERYDAY, WEEKLY 등)
}
