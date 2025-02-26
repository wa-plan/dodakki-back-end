package com.example.dodakki.goal.application;

import com.example.dodakki.Photo.domain.Photo;
import com.example.dodakki.Photo.domain.repository.PhotoRepository;
import com.example.dodakki.goal.application.dto.*;
import com.example.dodakki.goal.domain.*;
import com.example.dodakki.goal.domain.repository.GoalDateMapRepository;
import com.example.dodakki.goal.domain.repository.GoalDateRepository;
import com.example.dodakki.goal.domain.repository.GoalRepository;
import com.example.dodakki.goal.domain.repository.MandalartRepository;
import com.example.dodakki.goal.exception.MandalartException;
import com.example.dodakki.goal.exception.MandalartExceptionType;
import com.example.dodakki.user.domain.User;
import com.example.dodakki.user.domain.repository.UserRepository;
import com.example.dodakki.user.exception.UserException;
import com.example.dodakki.user.exception.UserExceptionType;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class MandalartService {

    private final MandalartRepository mandalartRepository;
    private final PhotoRepository photoRepository;
    private final UserRepository userRepository;
    private final GoalDateRepository goalDateRepository;
    private final GoalRepository goalRepository;
    private final GoalDateMapRepository goalDateMapRepository;

    public Long addMandalart(User user, MandalartAddRequest request) {
        User persistUser = userRepository.findById(user.getId()).orElseThrow(() -> new UserException(
            UserExceptionType.NOT_FOUND_MEMBER));
        Mandalart mandalart = new Mandalart(persistUser, request.getName(), request.getDescription(), request.getColor(), 0L, 0L);
        for(String picture : request.getPicture()) {
            Photo photo = new Photo(picture);
            photoRepository.save(photo);
            mandalart.addPhoto(photo);
        }
        mandalartRepository.save(mandalart);
        Optional<GoalDate> goalDate = goalDateRepository.findByDate(request.getDate());
        if(goalDate.isPresent()) {
            mandalart.setGoalDate(goalDate.get());
        }
        else{
            GoalDate goalDate1 =  goalDateRepository.save(new GoalDate(persistUser, request.getDate()));
            mandalart.setGoalDate(goalDate1);
        }
        return mandalart.getId();
    }

    public Mandalart getMandalart(User user, Long mandalartId) {
        userRepository.findById(user.getId()).orElseThrow(() -> new UserException(
            UserExceptionType.NOT_FOUND_MEMBER));
        return mandalartRepository.findById(mandalartId).orElseThrow(() -> new MandalartException(
            MandalartExceptionType.NOT_FOUND_MANDALART));
    }

    public Status updateProgress(User user, MandalartProgressRequest request){
        userRepository.findById(user.getId()).orElseThrow(() -> new UserException(
                UserExceptionType.NOT_FOUND_MEMBER));
        Mandalart mandalart = mandalartRepository.findById(request.getId()).orElseThrow(() -> new MandalartException(
                MandalartExceptionType.NOT_FOUND_MANDALART));
        mandalart.setStatus(request.getStatus());
        return mandalart.getStatus();
    }
    public BookmarkUpdateResponse updateBookmark(User user, BookmarkUpdateRequest request){
        userRepository.findById(user.getId()).orElseThrow(() -> new UserException(
                UserExceptionType.NOT_FOUND_MEMBER));
        Mandalart mandalart = mandalartRepository.findById(request.getId()).orElseThrow(() -> new MandalartException(
                MandalartExceptionType.NOT_FOUND_MANDALART));
        mandalart.setBookmark(request.getBookmark());
        return new BookmarkUpdateResponse(mandalart.getBookmark());
    }

    public void deleteMandalart(User user, Long mandalartId) {
        userRepository.findById(user.getId()).orElseThrow(() -> new UserException(
                UserExceptionType.NOT_FOUND_MEMBER));
        mandalartRepository.deleteById(mandalartId);
    }

    public MandalartAllResponse findMandalart(User user, Long mandalartId) {
        userRepository.findById(user.getId()).orElseThrow(() -> new UserException(
                UserExceptionType.NOT_FOUND_MEMBER));
        Mandalart mandalart = mandalartRepository.findById(mandalartId).orElseThrow(() -> new MandalartException(
                MandalartExceptionType.NOT_FOUND_MANDALART));
        return MandalartAllResponse.of(mandalart);
    }

    public List<MandalartListResponse> getMandalartList(User user) {
        User persistUser = userRepository.findById(user.getId()).orElseThrow(() -> new UserException(
                UserExceptionType.NOT_FOUND_MEMBER));
        return MandalartListResponse.of(persistUser);
    }

    public MandalartStatusNumResponse getNum(User user, Long mandalartId) {
        User persistUser = userRepository.findById(user.getId()).orElseThrow(() -> new UserException(
                UserExceptionType.NOT_FOUND_MEMBER));
        Mandalart mandalart = mandalartRepository.findById(mandalartId).orElseThrow(() -> new MandalartException(
                MandalartExceptionType.NOT_FOUND_MANDALART));
        int success = 0;
        int inProgress = 0;
        int failed = 0;
        for (Goal goal : mandalart.getGoals()) {
            for (GoalDateMap goalDateMap : goal.getGoalDateMapList()) {
                if (goalDateMap.getAttainment() == Status.SUCCESS){
                    success++;
                }
                else if (goalDateMap.getAttainment() == Status.IN_PROGRESS){
                    inProgress++;
                }
                else if (goalDateMap.getAttainment() == Status.FAIL){
                    failed++;
                }
            }
        }
        return new MandalartStatusNumResponse(success, inProgress, failed);
    }

    public DescriptionUpdateResponse updateDescription(User user, DescriptionUpdateRequest request){
        userRepository.findById(user.getId()).orElseThrow(() -> new UserException(
                UserExceptionType.NOT_FOUND_MEMBER));
        Mandalart mandalart = mandalartRepository.findById(request.getMandalartId()).orElseThrow(() -> new MandalartException(
                MandalartExceptionType.NOT_FOUND_MANDALART));
        mandalart.setDescription(request.getDescription());
        return new DescriptionUpdateResponse(mandalart.getDescription());
    }

    public NameUpdateResponse updateName(User user, NameUpdateRequest request){
        userRepository.findById(user.getId()).orElseThrow(() -> new UserException(
                UserExceptionType.NOT_FOUND_MEMBER));
        Mandalart mandalart = mandalartRepository.findById(request.getMandalartId()).orElseThrow(() -> new MandalartException(
                MandalartExceptionType.NOT_FOUND_MANDALART));
        mandalart.setName(request.getName());
        return new NameUpdateResponse(mandalart.getName());
    }

    public MandalartColorUpdateResponse updateColor(User user, MandalartColorUpdateRequest request){
        userRepository.findById(user.getId()).orElseThrow(() -> new UserException(
                UserExceptionType.NOT_FOUND_MEMBER));
        Mandalart mandalart = mandalartRepository.findById(request.getMandalartId()).orElseThrow(() -> new MandalartException(
                MandalartExceptionType.NOT_FOUND_MANDALART));
        mandalart.setColor(request.getColor());
        return new MandalartColorUpdateResponse(mandalart.getColor());
    }

    public void updateMandalartDate(User user, MandalartDateUpdateRequest request) {
        User persistUser = userRepository.findById(user.getId())
            .orElseThrow(() -> new UserException(UserExceptionType.NOT_FOUND_MEMBER));
        Mandalart mandalart = mandalartRepository.findById(request.getMandalartId())
            .orElseThrow(() -> new MandalartException(MandalartExceptionType.NOT_FOUND_MANDALART));

        // 기존 GoalDate
        GoalDate oldGoalDate = mandalart.getGoalDate();

        // 새로운 GoalDate
        GoalDate newGoalDate = goalDateRepository.findByUserAndDate(persistUser, request.getNewDate())
            .orElseGet(() -> goalDateRepository.save(new GoalDate(persistUser, request.getNewDate())));

        mandalart.setGoalDate(newGoalDate);

        for (Goal goal : mandalart.getGoals()) {
            List<GoalDateMap> goalDateMaps = goal.getGoalDateMapList();
            for (GoalDateMap map : goalDateMaps) {
                if (map.getGoalDate().equals(oldGoalDate)) {
                    map.setGoalDate(newGoalDate);
                }
            }
        }
    }

    public void deleteFutureDominoes(User user, Long mandalartId) {
        User persistUser = userRepository.findById(user.getId())
            .orElseThrow(() -> new UserException(UserExceptionType.NOT_FOUND_MEMBER));

        Mandalart mandalart = mandalartRepository.findById(mandalartId)
            .orElseThrow(() -> new MandalartException(MandalartExceptionType.NOT_FOUND_MANDALART));

        // 오늘 날짜 기준 이후의 GoalDateMap과 GoalDate 필터링 및 삭제
        List<Goal> goals = mandalart.getGoals();
        for (Goal goal : goals) {
            List<GoalDateMap> futureGoalDateMaps = goal.getGoalDateMapList().stream()
                .filter(goalDateMap -> goalDateMap.getGoalDate().getDate().isAfter(LocalDate.now()))
                .toList();

            // GoalDateMap 삭제
            for (GoalDateMap goalDateMap : futureGoalDateMaps) {
                GoalDate goalDate = goalDateMap.getGoalDate();
                goalDateMapRepository.delete(goalDateMap);

                if (goalDateMapRepository.findByGoalDate(goalDate).isEmpty()) {
                    goalDateRepository.delete(goalDate);
                }
            }
        }
    }

    @Transactional
    public void updateMandalartPicture(User user, MandalartPictureUpdateRequest request) {
        User persistUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new UserException(UserExceptionType.NOT_FOUND_MEMBER));

        Mandalart mandalart = mandalartRepository.findById(request.getMandalartId())
                .orElseThrow(() -> new MandalartException(MandalartExceptionType.NOT_FOUND_MANDALART));

        // 기존 사진 삭제
        mandalart.getPhotoList().clear();
        photoRepository.deleteAll(mandalart.getPhotoList());

        // 새로운 사진 추가
        for (String pictureUrl : request.getPictureUrls()) {
            Photo newPhoto = new Photo(pictureUrl);
            photoRepository.save(newPhoto);
            mandalart.addPhoto(newPhoto);
        }
    }

}
