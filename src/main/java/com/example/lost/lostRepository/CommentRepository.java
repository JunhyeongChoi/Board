package com.example.lost.lostRepository;

import com.example.lost.lostEntity.LostAnswer;
import com.example.lost.lostEntity.LostPost;
import com.example.lost.lostEntity.LostComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<LostComment, Long> {

    Page<LostComment> findAllByLostPost(LostPost lostPost, Pageable pageable);

    Page<LostComment> findAllByLostAnswer(LostAnswer question, Pageable pageable);
}