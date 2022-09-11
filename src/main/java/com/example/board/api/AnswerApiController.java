package com.example.board.api;

import com.example.board.form.AnswerForm;
import com.example.board.model.Answer;
import com.example.board.model.Board;
import com.example.board.repository.AnswerRepository;
import com.example.board.service.AnswerService;
import com.example.board.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RequestMapping("/api")
@RequiredArgsConstructor
@RestController
public class AnswerApiController {

    private final BoardService boardService;
    private final AnswerRepository answerRepository;
    private final AnswerService answerService;

    // 전체 댓글 조회 API
    @GetMapping("/answers")
    public List<Answer> all() {

        return answerRepository.findAll();
    }

    // id로 댓글 1개 조회 API
    @GetMapping("/answers/{id}")
    public Answer one(@PathVariable Long id) {

        return answerRepository.findById(id).orElse(null);
    }

    // 댓글 등록 API
    @PostMapping("/answers/{id}")
    public ResponseEntity<Answer> answerCreate(@PathVariable Long id, @RequestBody AnswerForm answerForm){
        Board board = boardService.getBoard(id);
        Answer answer = answerService.create(board, answerForm);

        return (answer != null) ? ResponseEntity.status(HttpStatus.OK).body(answer) : ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    // 댓글 수정 api
    @PutMapping("/answers/{id}")
    public Answer answerModify(@RequestBody Answer newAnswer, @PathVariable("id") Long id) {
        return answerRepository.findById(id)
                .map(answer -> {
                    answer.setContent(newAnswer.getContent());
                    return answerRepository.save(answer);
                })
                .orElseGet(() -> {
                    newAnswer.setId(id);
                    return answerRepository.save(newAnswer);
                });
    }

    // 댓글 삭제 API
    @DeleteMapping("/answers/{id}")
    void deleteAnswer(@PathVariable Long id) {
        answerRepository.deleteById(id);
    }

}
