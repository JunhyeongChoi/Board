package com.example.question.repository;

import com.example.question.entity.Answer;
import com.example.question.entity.Question;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnswerRepository extends JpaRepository<Answer, Long> {

    // Pageable 객체를 입력으로 받아 Page<Answer> 타입 객체를 리턴하는 findAll 메서드
    // 답변은 질문에 달려있다 따라서 질문에 달려있는 답변들을 페이징 처리하는거니 질문을 통해
    // 데이터를 조회할려한다
    Page<Answer> findAllByQuestion(Question question, Pageable pageable);
}