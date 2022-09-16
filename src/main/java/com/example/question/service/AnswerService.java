package com.example.question.service;

import com.example.question.DataNotFoundException;
import com.example.question.entity.Answer;
import com.example.question.entity.Question;
import com.example.question.repository.AnswerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class AnswerService {

    private final QuestionService questionService;
    private final AnswerRepository answerRepository;

    public Answer create(Question question, Answer answerForm) {
        Answer answer = new Answer();
        answer.setContent(answerForm.getContent());
        answer.setCreateDate(LocalDateTime.now());
        answer.setUsername(answerForm.getUsername());
        answer.setPassword(answerForm.getPassword());
        answer.setQuestion(question);

        answerRepository.save(answer);
        return answer;
    }

    // 답변 페이징 처리
    public Page<Answer> getList(int page, Long id) {
        Question question = questionService.getQuestion(id);
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("createDate"));
        Pageable pageable = PageRequest.of(page, 10, Sort.by(sorts));
        return this.answerRepository.findAllByQuestion(question, pageable);
    }

    public Answer getAnswer(Long id) {
        Optional<Answer> answer = this.answerRepository.findById(id);
        if (answer.isPresent()) {
            return answer.get();
        } else {
            throw new DataNotFoundException("answer not found");
        }
    }

    public void modify(Answer answer, String content) {
        answer.setContent(content);
        this.answerRepository.save(answer);
    }

    public Boolean delete(Answer answer) {
        this.answerRepository.delete(answer);;
        return true;
    }

}