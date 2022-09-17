package com.example.lost.lostService;

import com.example.lost.lostEntity.LostAnswer;
import com.example.lost.lostEntity.LostPost;
import com.example.lost.lostEntity.LostComment;
import com.example.lost.lostRepository.LostCommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class LostCommentService {

    @Autowired
    private LostCommentRepository lostCommentRepository;

    @Autowired
    private LostPostService lostPostService;

    @Autowired
    private LostAnswerService lostAnswerService;

    public LostComment create(LostAnswer lostAnswer, LostComment lostComment) {
        LostComment c = new LostComment();
        c.setContent(lostComment.getContent());
        c.setCreateDate(LocalDateTime.now());
        c.setQuestion(lostAnswer.getQuestion());
        c.setAnswer(lostAnswer);
        c.setUsername(lostComment.getUsername());
        c.setPassword(lostComment.getPassword());
        c = this.lostCommentRepository.save(c);
        return c;
    }

    // 답변 댓글
    public Page<LostComment> getAnswerCommentList(int page, Long id) {
        LostAnswer lostAnswer = lostAnswerService.getAnswer(id);
        Pageable pageable = PageRequest.of(page, 10);
        return this.lostCommentRepository.findAllByAnswer(lostAnswer, pageable);
    }

    // 질문 댓글
    public Page<LostComment> getQuestionCommentList(int page, Long id) {
        LostPost lostPost = lostPostService.getQuestion(id);
        Pageable pageable = PageRequest.of(page, 10);
        return this.lostCommentRepository.findAllByQuestion(lostPost, pageable);
    }

    public Optional<LostComment> getComment(Long id) {
        return this.lostCommentRepository.findById(id);
    }

    public LostComment modify(LostComment c, String content) {
        c.setContent(content);
        c = this.lostCommentRepository.save(c);
        return c;
    }

    public Boolean delete(LostComment c) {

        this.lostCommentRepository.delete(c);
        return true;
    }
}