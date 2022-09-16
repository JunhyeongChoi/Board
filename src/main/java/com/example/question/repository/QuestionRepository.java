package com.example.question.repository;

import com.example.question.entity.Question;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Long> {

    Page<Question> findBySubjectContainingOrContentContaining(String searchText, String searchText1, Pageable pageable);
}
