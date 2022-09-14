package com.example.board.api;

import com.example.board.model.Answer;
import com.example.board.model.Board;
import com.example.board.repository.AnswerRepository;
import com.example.board.service.AnswerService;
import com.example.board.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

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
    public ResponseEntity<Answer> one(@PathVariable Long id) {

        Answer answer = answerRepository.findById(id).orElse(null);
        if (answer == null) return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(answer, HttpStatus.OK);
    }

    // 댓글 등록 API
    @PostMapping("/answers/{id}")
    public ResponseEntity<Answer> answerCreate(@PathVariable Long id, @Valid @RequestBody Answer answerForm){

        if (answerForm.getNickname() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "닉네임 입력 필수");
        }

        Board board = this.boardService.getBoard(id);
        if (board == null) return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        Answer answer = this.answerService.create(board, answerForm);
        if (answer == null) return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        return (answer != null) ? ResponseEntity.status(HttpStatus.OK).body(answer) : ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    // 댓글 수정 api
    @PutMapping("/answers/{id}")
    public ResponseEntity<Answer> answerModify(@Valid @RequestBody Answer newAnswer, @PathVariable("id") Long id) {

        if (newAnswer.getPassword().equals(newAnswer.getPassword())) {

        return answerRepository.findById(id)
                .map(answer -> {
                    answer.setContent(newAnswer.getContent());
                    answerRepository.save(answer);
                    return ResponseEntity.status(HttpStatus.OK).body(answer);
                })
                .orElseGet(() -> {
                    newAnswer.setId(id);
                    answerRepository.save(newAnswer);
                    return ResponseEntity.status(HttpStatus.OK).body(newAnswer);
                });

        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "비밀번호 불일치");
        }
    }

    // 댓글 삭제 API
    @DeleteMapping("/answers/{id}")
    public ResponseEntity deleteComment(@PathVariable("id") Long id, String password) {
        Answer answer = this.answerService.getAnswer(id);
        if (answer == null) return new ResponseEntity(HttpStatus.NOT_FOUND);

        if (password == null || password.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "비밀번호 입력 필수");
        }

        if (password.equals(answer.getPassword())) {
            this.answerService.delete(answer);
            return ResponseEntity.ok("정상적으로 삭제되었습니다");
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "비밀번호 불일치");
        }

    }

}
