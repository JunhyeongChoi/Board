package com.example.question.repository;

import com.example.question.entity.Answer;
import com.example.question.entity.Question;
import com.example.question.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    Page<Comment> findAllByQuestion(Question question, Pageable pageable);

    Page<Comment> findAllByAnswer(Answer question, Pageable pageable);
}