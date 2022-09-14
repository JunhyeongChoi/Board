package com.example.board.service;

import com.example.board.model.Answer;
import com.example.board.model.Board;
import com.example.board.model.Comment;
import com.example.board.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    public Comment create(Answer answer, Comment comment) {
        Comment c = new Comment();
        c.setContent(comment.getContent());
        c.setCreateDate(LocalDateTime.now());
        c.setBoard(answer.getBoard());
        c.setAnswer(answer);
        c.setNickname(comment.getNickname());
        c.setPassword(comment.getPassword());
        c = this.commentRepository.save(c);
        return c;
    }

    public Optional<Comment> getComment(Long id) {
        return this.commentRepository.findById(id);
    }

    public Comment modify(Comment c, String content) {
        c.setContent(content);
        c = this.commentRepository.save(c);
        return c;
    }

    public void delete(Comment c) {
        this.commentRepository.delete(c);
    }
}