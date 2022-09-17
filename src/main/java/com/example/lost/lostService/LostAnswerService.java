package com.example.lost.lostService;

import com.example.lost.DataNotFoundException;
import com.example.lost.lostEntity.LostAnswer;
import com.example.lost.lostEntity.LostPost;
import com.example.lost.lostRepository.AnswerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class LostAnswerService {

    private final LostPostService lostPostService;
    private final AnswerRepository answerRepository;

    public LostAnswer create(LostPost lostPost, LostAnswer lostAnswerForm) {
        LostAnswer lostAnswer = new LostAnswer();
        lostAnswer.setContent(lostAnswerForm.getContent());
        lostAnswer.setCreateDate(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss")));
        lostAnswer.setUsername(lostAnswerForm.getUsername());
        lostAnswer.setPassword(lostAnswerForm.getPassword());
        lostAnswer.setLostPost(lostPost);

        answerRepository.save(lostAnswer);
        return lostAnswer;
    }

    // 답변 페이징 처리
    public Page<LostAnswer> getList(int page, Long id) {
        LostPost lostPost = lostPostService.getQuestion(id);
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("createDate"));
        Pageable pageable = PageRequest.of(page, 10, Sort.by(sorts));
        return this.answerRepository.findAllByLostPost(lostPost, pageable);
    }

    public LostAnswer getAnswer(Long id) {
        Optional<LostAnswer> answer = this.answerRepository.findById(id);
        if (answer.isPresent()) {
            return answer.get();
        } else {
            throw new DataNotFoundException("answer not found");
        }
    }

    public void modify(LostAnswer lostAnswer, String content) {
        lostAnswer.setContent(content);
        this.answerRepository.save(lostAnswer);
    }

    public Boolean delete(LostAnswer lostAnswer) {
        this.answerRepository.delete(lostAnswer);;
        return true;
    }

}