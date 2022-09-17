package com.example.lost.lostRepository;

import com.example.lost.lostEntity.LostAnswer;
import com.example.lost.lostEntity.LostPost;
import com.example.lost.lostEntity.LostComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LostCommentRepository extends JpaRepository<LostComment, Long> {

    Page<LostComment> findAllByQuestion(LostPost lostPost, Pageable pageable);

    Page<LostComment> findAllByAnswer(LostAnswer question, Pageable pageable);
}