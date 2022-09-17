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

    private String createDate;

    private String username;

    public LostCommentDto(Long id, String content, String createDate, String username) {
        this.id = id;
        this.content = content;
        this.createDate = createDate;
        this.username = username;
    }
}