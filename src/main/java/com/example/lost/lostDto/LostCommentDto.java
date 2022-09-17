package com.example.lost.lostDto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class LostCommentDto {

    private Long id;

    private String content;

    private LocalDateTime createDate;

    private String nickname;


    public LostCommentDto(Long id, String content, LocalDateTime createDate, String nickname) {
        this.id = id;
        this.content = content;
        this.createDate = createDate;
        this.nickname = nickname;
    }
}