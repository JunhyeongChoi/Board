package com.example.lost.lostRepository;

import com.example.lost.lostEntity.LostPost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LostPostRepository extends JpaRepository<LostPost, Long> {

    Page<LostPost> findBySubjectContainingOrContentContaining(String searchText, String searchText1, Pageable pageable);
}
