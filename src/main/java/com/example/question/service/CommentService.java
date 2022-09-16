package com.example.question.service;

import com.example.question.entity.Answer;
import com.example.question.entity.Question;
import com.example.question.entity.Comment;
import com.example.question.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private AnswerService answerService;

    public Comment create(Answer answer, Comment comment) {
        Comment c = new Comment();
        c.setContent(comment.getContent());
        c.setCreateDate(LocalDateTime.now());
        c.setQuestion(answer.getQuestion());
        c.setAnswer(answer);
        c.setUsername(comment.getUsername());
        c.setPassword(comment.getPassword());
        c = this.commentRepository.save(c);
        return c;
    }

    // 답변 댓글
    public Page<Comment> getAnswerCommentList(int page, Long id) {
        Answer answer = answerService.getAnswer(id);
        Pageable pageable = PageRequest.of(page, 10);
        return this.commentRepository.findAllByAnswer(answer, pageable);
    }

    // 질문 댓글
    public Page<Comment> getQuestionCommentList(int page, Long id) {
        Question question = questionService.getQuestion(id);
        Pageable pageable = PageRequest.of(page, 10);
        return this.commentRepository.findAllByQuestion(question, pageable);
    }

    public Optional<Comment> getComment(Long id) {
        return this.commentRepository.findById(id);
    }

    public Comment modify(Comment c, String content) {
        c.setContent(content);
        c = this.commentRepository.save(c);
        return c;
    }

    public Boolean delete(Comment c) {

        this.commentRepository.delete(c);
        return true;
    }
}