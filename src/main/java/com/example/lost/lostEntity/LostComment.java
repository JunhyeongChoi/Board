package com.example.lost.lostEntity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class LostComment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT")
    @NotNull(message = "내용은 필수 항목입니다.")
    @Size(min=1, message = "내용은 최소 한 글자 이상이어야 합니다.")
    private String content;

    private LocalDateTime createDate;

    @Size(min=1, message = "닉네임은 한 글자 이상이어야 합니다.")
    private String username;

    @NotNull(message = "비밀번호는 필수 항목입니다.")
    @Size(min=4, max=50, message = "비밀번호는 네 자리 이상이어야 합니다.")
    private String password;

    @JsonIgnore
    @ManyToOne
    private LostPost question;

    @JsonIgnore
    @ManyToOne
    private LostAnswer answer;

}