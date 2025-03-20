package com.example.dodakki.goal.application.dto;

import com.example.dodakki.goal.domain.Repetition;
import com.example.dodakki.goal.domain.Status;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
@Getter
public class GoalResponse {

    private Long id;
    private Long thirdGoalId;
    private String goalName;
    private String color;
    private String thirdGoal;
    private Status attainment;
    private Repetition repetition;

//    public static GoalResponse of(Goal goal){
//        return new GoalResponse(goal.getId(), goal.getName(), goal.getThirdGoal().getSecondGoal().getColor(), goal.getThirdGoal().getName(), goal.(), goal.getRepetition());
//    }
//    public static List<GoalResponse> listOf(List<Goal> goals) {
//        List<GoalResponse> goalResponses = new ArrayList<>();
//
//        for(Goal goal : goals){
//            goalResponses.add(of(goal));
//        }
//        return goalResponses;
//    }
}
