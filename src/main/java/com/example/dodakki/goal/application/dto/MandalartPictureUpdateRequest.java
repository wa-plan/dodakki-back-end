package com.example.dodakki.goal.application.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class MandalartPictureUpdateRequest {
    @NotNull
    private Long mandalartId; // 수정할 만다라트 ID

    @NotNull
    private List<String> pictureUrls; // 업데이트할 사진 URL 리스트
}
