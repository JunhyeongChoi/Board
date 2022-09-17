package com.example.lost.lostApi;

import com.example.lost.lostDto.LostSuccessDto;
import com.example.lost.lostEntity.LostAnswer;
import com.example.lost.lostEntity.LostPost;
import com.example.lost.lostForm.CreateForm;
import com.example.lost.lostForm.ModifyForm;
import com.example.lost.lostForm.LostDeleteForm;
import com.example.lost.lostRepository.AnswerRepository;
import com.example.lost.lostService.LostAnswerService;
import com.example.lost.lostService.LostPostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;

@RequestMapping("/lost")
@RequiredArgsConstructor
@RestController
public class LostAnswerApiController {

    private final LostPostService lostPostService;
    private final AnswerRepository answerRepository;
    private final LostAnswerService lostAnswerService;

//    // 전체 댓글 조회 API
//    @GetMapping("/answers")
//    public List<Answer> all() {
//
//        return answerRepository.findAll();
//    }
//
//    // id로 댓글 1개 조회 API
//    @GetMapping("/answers/{id}")
//    public ResponseEntity<Answer> one(@PathVariable Long id) {
//
//        Answer answer = answerRepository.findById(id).orElse(null);
//        if (answer == null) return new ResponseEntity<>(HttpStatus.NOT_FOUND);
//        return new ResponseEntity<>(answer, HttpStatus.OK);
//    }

    // 댓글 등록 API
    @PostMapping("/answers/{id}")
    public ResponseEntity<CreateForm> answerCreate(@PathVariable Long id, @Valid @RequestBody LostAnswer lostAnswerForm){

        if (lostAnswerForm.getUsername() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "닉네임 입력 필수");
        }

        LostPost lostPost = this.lostPostService.getQuestion(id);
        if (lostPost == null) return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        LostAnswer lostAnswer = this.lostAnswerService.create(lostPost, lostAnswerForm);
        if (lostAnswer == null) return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        CreateForm createForm = new CreateForm(lostAnswerForm.getContent(), lostAnswerForm.getUsername(), lostAnswer.getCreateDate());

        return (lostAnswer != null) ? ResponseEntity.status(HttpStatus.OK).body(createForm) : ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    // 댓글 수정 api
    @PutMapping("/answers/{id}")
    public ResponseEntity<ModifyForm> answerModify(@Valid @RequestBody LostAnswer newLostAnswer, @PathVariable("id") Long id) {

        LostAnswer exLostAnswer = answerRepository.findById(id).orElse(null);
        if (exLostAnswer == null) return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        if (newLostAnswer.getPassword().equals(exLostAnswer.getPassword())) {

        return answerRepository.findById(id)
                .map(answer -> {
                    answer.setContent(newLostAnswer.getContent());
                    answerRepository.save(answer);
                    ModifyForm modifyForm = new ModifyForm(newLostAnswer.getContent(), exLostAnswer.getCreateDate());
                    return ResponseEntity.status(HttpStatus.OK).body(modifyForm);
                })
                .orElseGet(() -> {
                    newLostAnswer.setId(id);
                    answerRepository.save(newLostAnswer);
                    ModifyForm modifyForm = new ModifyForm(newLostAnswer.getContent(), exLostAnswer.getCreateDate());
                    return ResponseEntity.status(HttpStatus.OK).body(modifyForm);
                });

        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "비밀번호 불일치");
        }
    }

    // 댓글 삭제 API
    @DeleteMapping("/answers/{id}")
    public ResponseEntity deleteComment(@PathVariable("id") Long id, @Valid @RequestBody LostDeleteForm lostDeleteForm) {
        LostAnswer lostAnswer = this.lostAnswerService.getAnswer(id);
        if (lostAnswer == null) return new ResponseEntity(HttpStatus.NOT_FOUND);

        if (lostDeleteForm.getPassword() == null || lostDeleteForm.getPassword().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "비밀번호 입력 필수");
        }

        if (!lostAnswer.getPassword().equals(lostDeleteForm.getPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제권한이 없습니다.");
        }

        LostSuccessDto lostSuccessDto = new LostSuccessDto(this.lostAnswerService.delete(lostAnswer));
        return ResponseEntity.ok(lostSuccessDto);

    }

}
