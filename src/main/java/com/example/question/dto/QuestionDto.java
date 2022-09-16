package com.example.question.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class QuestionDto {

    private Long id;

    private String title;

    private String content;

    private LocalDateTime createDate;

    private String nickname;

    private Boolean isLost;

    public QuestionDto(Long id, String title, String content, LocalDateTime createDate,
                       String nickname, Boolean isLost) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.createDate = createDate;
        this.nickname = nickname;
        this.isLost = isLost;
    }


}