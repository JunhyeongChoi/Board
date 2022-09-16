package com.example.board.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.CascadeType.ALL;

@Entity
@Data
public class Board {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Size(min=2, max=50, message = "제목은 2글자 이상, 50글자 이하여야 합니다.")
    private String title;

    @NotNull(message = "내용은 필수 항목입니다.")
    @Size(min=1, message = "내용은 최소 한 글자 이상이어야 합니다.")
    private String content;

    @Column
    private LocalDateTime date;

    private Boolean isLost;   // 분실, 발견

    private String filename;

    private String filepath;

    @Size(min=1, message = "닉네임은 한 글자 이상이어야 합니다.")
    private String nickname;

    @NotNull(message = "비밀번호는 필수 항목입니다.")
    @Size(min=4, max=50, message = "비밀번호는 네 자리 이상이어야 합니다.")
    @JsonIgnore
    private String password;

    @JsonIgnore
    @OneToMany(mappedBy = "board", cascade = CascadeType.REMOVE)
    private List<Answer> answerList;

    @JsonIgnore
    @OneToMany(mappedBy = "board")
    private List<Comment> commentList;

}

