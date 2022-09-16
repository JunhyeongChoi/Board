package com.example.question.dto;

import com.example.question.entity.Comment;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class AnswerDto {

    private Long id;

    private String content;

    private LocalDateTime createDate;

    private String nickname;

    private List<Comment> commentList;


    public AnswerDto(Long id, String content, LocalDateTime createDate,
                     String nickname, List<Comment> commentList) {
        this.id = id;
        this.content = content;
        this.createDate = createDate;
        this.nickname = nickname;
        this.commentList = commentList;
    }
}