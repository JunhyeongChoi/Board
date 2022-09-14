package com.example.board.model;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Answer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT")
    @NotNull(message = "내용은 필수 항목입니다.")
    @Size(min=1, message = "내용은 최소 한 글자 이상이어야 합니다.")
    private String content;

    private LocalDateTime date;

    @Size(min=1, message = "닉네임은 한 글자 이상이어야 합니다.")
    private String nickname;

    @NotNull(message = "비밀번호는 필수 항목입니다.")
    @Size(min=4, max=50, message = "비밀번호는 네 자리 이상이어야 합니다.")
    private String password;

    @ManyToOne
    private Board board;

    @JsonIgnore
    @OneToMany(mappedBy = "answer", cascade = CascadeType.REMOVE)
    private List<Comment> commentList;

}