package com.example.question.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class CommentDto {

    private Long id;

    private String content;

    private LocalDateTime createDate;

    private String nickname;


    public CommentDto(Long id, String content, LocalDateTime createDate, String nickname) {
        this.id = id;
        this.content = content;
        this.createDate = createDate;
        this.nickname = nickname;
    }
}