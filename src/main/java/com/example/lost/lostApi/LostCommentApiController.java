package com.example.lost.lostApi;

import com.example.lost.lostDto.LostSuccessDto;
import com.example.lost.lostEntity.LostAnswer;
import com.example.lost.lostEntity.LostComment;
import com.example.lost.lostForm.LostDeleteForm;
import com.example.lost.lostRepository.LostCommentRepository;
import com.example.lost.lostService.LostAnswerService;
import com.example.lost.lostService.LostCommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;

@RequestMapping("/lost")
@RestController
public class LostCommentApiController {

    @Autowired
    private LostCommentService lostCommentService;

    @Autowired
    private LostCommentRepository lostCommentRepository;

    @Autowired
    private LostAnswerService lostAnswerService;

//    // 전체 대댓글 조회 API
//    @GetMapping("/comments")
//    public List<Comment> all() {
//
//        return commentRepository.findAll();
//    }
//
//    // id로 대댓글 1개 조회 API
//    @GetMapping("/comments/{id}")
//    public ResponseEntity<Comment> one(@PathVariable Long id) {
//
//        Comment comment = commentRepository.findById(id).orElse(null);
//        if (comment == null) return new ResponseEntity<>(HttpStatus.NOT_FOUND);
//        return new ResponseEntity<>(comment, HttpStatus.OK);
//    }

    // 대댓글 등록 API
    @PostMapping(value = "/comments/{id}")
    public ResponseEntity<LostComment> createQuestionComment(@PathVariable("id") Long id, @Valid @RequestBody LostComment lostCommentForm) {

        if (lostCommentForm.getUsername() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "닉네임 입력 필수");
        }

        LostAnswer lostAnswer = lostAnswerService.getAnswer(id);
        if (lostAnswer == null) return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        LostComment lostComment = lostCommentService.create(lostAnswer, lostCommentForm);
        if (lostComment == null) return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        return (lostComment != null) ? ResponseEntity.status(HttpStatus.OK).body(lostComment) : ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    // 대댓글 수정 api
    @PutMapping("/comments/{id}")
    public ResponseEntity<LostComment> answerModify(@Valid @RequestBody LostComment newLostComment, @PathVariable("id") Long id) {

        LostComment exLostComment = lostCommentRepository.findById(id).orElse(null);
        if (exLostComment == null) return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        if (newLostComment.getPassword().equals(exLostComment.getPassword())) {

        return lostCommentRepository.findById(id)
                .map(comment -> {
                    comment.setContent(newLostComment.getContent());
                    lostCommentRepository.save(comment);
                    return ResponseEntity.status(HttpStatus.OK).body(comment);
                })
                .orElseGet(() -> {
                    newLostComment.setId(id);
                    lostCommentRepository.save(newLostComment);
                    return ResponseEntity.status(HttpStatus.OK).body(newLostComment);
                });

        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "비밀번호 불일치");
        }
    }

    // 대댓글 삭제 API
    @DeleteMapping("/comments/{id}")
    public ResponseEntity deleteComment(@PathVariable("id") Long id, @Valid @RequestBody LostDeleteForm lostDeleteForm) {
        LostComment lostComment = this.lostCommentService.getComment(id).orElse(null);
        if (lostComment == null) return new ResponseEntity(HttpStatus.NOT_FOUND);

        if (lostDeleteForm.getPassword() == null || lostDeleteForm.getPassword().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "비밀번호 입력 필수");
        }

        if (!lostComment.getPassword().equals(lostDeleteForm.getPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제권한이 없습니다.");
        }

        LostSuccessDto lostSuccessDto = new LostSuccessDto(this.lostCommentService.delete(lostComment));
        return ResponseEntity.ok(lostSuccessDto);
    }
}