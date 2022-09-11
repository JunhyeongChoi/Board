package com.example.board.model;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String content;

    private LocalDateTime createDate;

    @ManyToOne
    private Board board;

    @ManyToOne
    private Answer answer;

    public Long getBoardId() {
        Long result = null;
        if (this.board != null) {
            result = this.board.getId();
        } else if (this.answer != null) {
            result = this.answer.getBoard().getId();
        }
        return result;
    }

}