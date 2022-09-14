package com.example.board.model;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import javax.persistence.*;

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
    private String content;

    private LocalDateTime date;

    @ManyToOne
    private Board board;

    @JsonIgnore
    @OneToMany(mappedBy = "answer", cascade = CascadeType.REMOVE)
    private List<Comment> commentList;

}