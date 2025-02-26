package com.example.dodakki.goal.application;

import com.example.dodakki.goal.application.dto.*;
import com.example.dodakki.goal.domain.*;
import com.example.dodakki.goal.domain.repository.*;
import com.example.dodakki.goal.exception.*;
import com.example.dodakki.user.domain.User;
import com.example.dodakki.user.domain.repository.UserRepository;
import com.example.dodakki.user.exception.UserException;
import com.example.dodakki.user.exception.UserExceptionType;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class GoalService {

    private final UserRepository userRepository;
    private final ThirdGoalRepository thirdGoalRepository;
    private final GoalDateRepository goalDateRepository;
    private final GoalDateMapRepository goalDateMapRepository;
    private final GoalRepository goalRepository;
    private final MandalartRepository mandalartRepository;

    public Long addGoal(User user, GoalAddRequest goalAddRequest){
        User persistUser = userRepository.findById(user.getId()).orElseThrow(() -> new UserException(
                UserExceptionType.NOT_FOUND_MEMBER));
        ThirdGoal thirdGoal = thirdGoalRepository.findById(goalAddRequest.getThirdGoalId()).orElseThrow(() -> new GoalException(
                GoalExceptionType.NOT_FOUND_GOAL));
        Mandalart mandalart = mandalartRepository.findById(thirdGoal.getSecondGoal().getMandalart().getId()).orElseThrow(() -> new MandalartException(
                MandalartExceptionType.NOT_FOUND_MANDALART));
        Goal goal = new Goal(goalAddRequest.getName(), mandalart, thirdGoal, goalAddRequest.getRepetition());
        goalRepository.save(goal);
        List<GoalDateMap> goalDateMapList = goalAddRequest.getDates().stream()
                .map(date -> {
                    GoalDateMap goalDateMap;
                    Optional<GoalDate> goalDate = goalDateRepository.findByUserAndDate(persistUser, date);
                    if(goalDate.isPresent()){
                        goalDateMap = goalDateMapRepository.save(new GoalDateMap(goal, goalDate.get(), Status.NONE));
                    }
                    else{
                        GoalDate goalDate1 = goalDateRepository.save(new GoalDate(persistUser, date));
                        goalDateMap = goalDateMapRepository.save(new GoalDateMap(goal, goalDate1, Status.NONE));
                    }
                    return goalDateMap;
                }).toList();
        goal.setGoalDateMapList(goalDateMapList);
        mandalart.setGoalCount(mandalart.getGoalCount() + 1);
        return goal.getId();
    }

    public void updateGoalStatus(User user, GoalUpdateStatusRequest request){
        userRepository.findById(user.getId()).orElseThrow(() -> new UserException(
                UserExceptionType.NOT_FOUND_MEMBER));
        Goal goal = goalRepository.findById(request.getGoalId()).orElseThrow(() -> new GoalException(
                GoalExceptionType.NOT_FOUND_GOAL));
        Mandalart mandalart = mandalartRepository.findById(goal.getMandalart().getId()).orElseThrow(() -> new MandalartException(
                MandalartExceptionType.NOT_FOUND_MANDALART));
        GoalDate goalDate = goalDateRepository.findByDate(request.getDate()).orElseThrow(() -> new GoalDateException(
                GoalDateExceptionType.NOT_FOUND_GOAL_DATE));
        GoalDateMap goalDateMap = goalDateMapRepository.findByGoalAndGoalDate(goal, goalDate).orElseThrow(() -> new GoalDateMapException(
                GoalDateMapExceptionType.NOT_FOUND_GOAL_DATE_MAP));
        goalDateMap.setAttainment(request.getAttainment());
        if(request.getAttainment() == Status.SUCCESS){
            mandalart.setAttainmentCount(mandalart.getAttainmentCount() + 1);
        }
    }

    public void updateGoal(User user, GoalUpdateRequest request){
        userRepository.findById(user.getId()).orElseThrow(() -> new UserException(
                UserExceptionType.NOT_FOUND_MEMBER));
        Goal goal = goalRepository.findById(request.getGoalId()).orElseThrow(() -> new GoalException(
                GoalExceptionType.NOT_FOUND_GOAL));
        goal.setName(request.getNewGoal());
    }

    public void deleteGoal(User user, Long goalId){
        userRepository.findById(user.getId()).orElseThrow(() -> new UserException(
                UserExceptionType.NOT_FOUND_MEMBER));
        Goal goal = goalRepository.findById(goalId).orElseThrow(() -> new GoalException(
                GoalExceptionType.NOT_FOUND_GOAL));
        Mandalart mandalart = mandalartRepository.findById(goal.getMandalart().getId()).orElseThrow(() -> new MandalartException(
                MandalartExceptionType.NOT_FOUND_MANDALART));
        mandalart.setGoalCount(mandalart.getGoalCount() - 1);
        goalRepository.delete(goal);
    }
    public void deleteDateGoal(User user, Long goalId, LocalDate date){
        User persistUser = userRepository.findById(user.getId()).orElseThrow(() -> new UserException(
                UserExceptionType.NOT_FOUND_MEMBER));
        GoalDate goalDate = goalDateRepository.findByUserAndDate(persistUser, date).orElseThrow(() -> new GoalDateException(
                GoalDateExceptionType.NOT_FOUND_GOAL_DATE));
        Goal goal = goalRepository.findById(goalId).orElseThrow(() -> new GoalException(
                GoalExceptionType.NOT_FOUND_GOAL));
        goalDateMapRepository.deleteByGoalAndGoalDate(goal, goalDate).orElseThrow(() -> new GoalDateMapException(
                GoalDateMapExceptionType.NOT_FOUND_GOAL_DATE_MAP));
    }
    public List<GoalResponse> getGoal(User user, LocalDate request){
        User persistUser = userRepository.findById(user.getId()).orElseThrow(() -> new UserException(
                UserExceptionType.NOT_FOUND_MEMBER));
        GoalDate goalDate = goalDateRepository.findByUserAndDate(persistUser, request).orElseThrow(() -> new GoalDateException(
                GoalDateExceptionType.NOT_FOUND_GOAL_DATE));
        List<GoalDateMap> goalDateMap = goalDateMapRepository.findByGoalDate(goalDate);
        return goalDateMap.stream().map(
                goalDateMap1 -> {
                    Goal goal = goalRepository.findById(goalDateMap1.getGoal().getId()).orElseThrow(() -> new GoalException(
                        GoalExceptionType.NOT_FOUND_GOAL));
                    return new GoalResponse(goal.getId(), goal.getName(), goal.getThirdGoal().getSecondGoal().getColor(), goal.getThirdGoal().getName(), goalDateMap1.getAttainment(), goal.getRepetition());
                }
        ).toList();
    }

    public void updateGoalDate(User user, GoalDateUpdateRequest request) {
        User persistUser = userRepository.findById(user.getId())
            .orElseThrow(() -> new UserException(UserExceptionType.NOT_FOUND_MEMBER));

        Goal goal = goalRepository.findById(request.getGoalId())
            .orElseThrow(() -> new GoalException(GoalExceptionType.NOT_FOUND_GOAL));

        // 기존 GoalDate 찾기
        GoalDate oldGoalDate = goalDateRepository.findByUserAndDate(persistUser, request.getOldDate())
            .orElseThrow(() -> new GoalDateException(GoalDateExceptionType.NOT_FOUND_GOAL_DATE));

        // GoalDateMap 삭제
        GoalDateMap goalDateMap = goalDateMapRepository.findByGoalAndGoalDate(goal, oldGoalDate)
            .orElseThrow(() -> new GoalDateMapException(GoalDateMapExceptionType.NOT_FOUND_GOAL_DATE_MAP));

        goalDateMapRepository.delete(goalDateMap);

        // 새로운 GoalDate 추가
        GoalDate newGoalDate = goalDateRepository.findByUserAndDate(persistUser, request.getNewDate())
            .orElseGet(() -> goalDateRepository.save(new GoalDate(persistUser, request.getNewDate())));

        goalDateMapRepository.save(new GoalDateMap(goal, newGoalDate, goalDateMap.getAttainment()));
    }

    @Transactional
    public void updateDominoGoalDate(User user, GoalDominoDateUpdateRequest request) {
        User persistUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new UserException(UserExceptionType.NOT_FOUND_MEMBER));

        Goal goal = goalRepository.findById(request.getGoalId())
                .orElseThrow(() -> new GoalException(GoalExceptionType.NOT_FOUND_GOAL));

        // 기존 날짜 찾기
        GoalDate oldGoalDate = goalDateRepository.findByUserAndDate(persistUser, request.getOldDate())
                .orElseThrow(() -> new GoalDateException(GoalDateExceptionType.NOT_FOUND_GOAL_DATE));

        // 기존 날짜에 연결된 모든 GoalDateMap 찾기
        List<GoalDateMap> goalDateMaps = goalDateMapRepository.findByGoalDate(oldGoalDate);

        if (goalDateMaps.isEmpty()) {
            throw new GoalDateMapException(GoalDateMapExceptionType.NOT_FOUND_GOAL_DATE_MAP);
        }

        // 새로운 날짜에 해당하는 GoalDate 찾기 또는 생성
        GoalDate newGoalDate = goalDateRepository.findByUserAndDate(persistUser, request.getNewDate())
                .orElseGet(() -> goalDateRepository.save(new GoalDate(persistUser, request.getNewDate())));

        // 모든 연결된 목표 날짜 업데이트 (도미노 효과)
        for (GoalDateMap goalDateMap : goalDateMaps) {
            Goal currentGoal = goalDateMap.getGoal();

            // 기존 날짜 매핑 삭제
            goalDateMapRepository.delete(goalDateMap);

            // 새 날짜에 대한 GoalDateMap 새로 저장
            GoalDateMap newGoalDateMap = new GoalDateMap(currentGoal, newGoalDate, goalDateMap.getAttainment());
            goalDateMapRepository.save(newGoalDateMap);
        }
    }

    @Transactional
    public void deleteFutureDominoGoals(User user, GoalDominoDeleteRequest request) {
        User persistUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new UserException(UserExceptionType.NOT_FOUND_MEMBER));

        Goal goal = goalRepository.findById(request.getGoalId())
                .orElseThrow(() -> new GoalException(GoalExceptionType.NOT_FOUND_GOAL));

        // 오늘 날짜 기준으로 목표 삭제 진행
        LocalDate today = LocalDate.now();

        // 해당 목표와 연결된 모든 GoalDateMap 가져오기
        List<GoalDateMap> goalDateMaps = goalDateMapRepository.findByGoal(goal);

        if (goalDateMaps.isEmpty()) {
            throw new GoalDateMapException(GoalDateMapExceptionType.NOT_FOUND_GOAL_DATE_MAP);
        }

        // 오늘 이후 날짜만 필터링해서 삭제
        for (GoalDateMap goalDateMap : goalDateMaps) {
            if (goalDateMap.getGoalDate().getDate().isAfter(today)) {
                goalDateMapRepository.delete(goalDateMap);
            }
        }
    }

    @Transactional
    public void fullUpdateGoal(User user, GoalFullUpdateRequest request) {
        User persistUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new UserException(UserExceptionType.NOT_FOUND_MEMBER));

        ThirdGoal thirdGoal = thirdGoalRepository.findById(request.getThirdGoalId())
                .orElseThrow(() -> new GoalException(GoalExceptionType.NOT_FOUND_GOAL));

        Mandalart mandalart = mandalartRepository.findById(thirdGoal.getSecondGoal().getMandalart().getId())
                .orElseThrow(() -> new MandalartException(MandalartExceptionType.NOT_FOUND_MANDALART));

        // 기존 Goal 찾기
        List<Goal> existingGoals = goalRepository.findAll().stream()
                .filter(goal -> goal.getThirdGoal().getId().equals(request.getThirdGoalId()))
                .toList();

        if (existingGoals.isEmpty()) {
            throw new GoalException(GoalExceptionType.NOT_FOUND_GOAL);
        }

        for (Goal goal : existingGoals) {
            // 이름과 반복 주기 업데이트
            goal.setName(request.getName());
            goal.setRepetition(request.getRepetition());

            // 기존 GoalDateMap 삭제
            goalDateMapRepository.deleteAll(goal.getGoalDateMapList());

            // 새로운 날짜로 GoalDateMap 재설정
            List<GoalDateMap> newGoalDateMaps = request.getDates().stream()
                    .map(date -> {
                        GoalDate goalDate = goalDateRepository.findByUserAndDate(persistUser, date)
                                .orElseGet(() -> goalDateRepository.save(new GoalDate(persistUser, date)));
                        return goalDateMapRepository.save(new GoalDateMap(goal, goalDate, Status.NONE));
                    }).toList();

            goal.setGoalDateMapList(newGoalDateMaps);
        }
    }


}
