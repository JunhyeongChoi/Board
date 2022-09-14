package com.example.board.api;

import com.example.board.model.Answer;
import com.example.board.model.Comment;
import com.example.board.repository.CommentRepository;
import com.example.board.service.AnswerService;
import com.example.board.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RequestMapping("/api")
@RestController
public class CommentApiController {

    @Autowired
    private CommentService commentService;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private AnswerService answerService;

    // 전체 대댓글 조회 API
    @GetMapping("/comments")
    public List<Comment> all() {

        return commentRepository.findAll();
    }

    // id로 대댓글 1개 조회 API
    @GetMapping("/comments/{id}")
    public ResponseEntity<Comment> one(@PathVariable Long id) {

        Comment comment = commentRepository.findById(id).orElse(null);
        if (comment == null) return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(comment, HttpStatus.OK);
    }

    // 대댓글 등록 API
    @PostMapping(value = "/comments/{id}")
    public ResponseEntity<Comment> createBoardComment(@PathVariable("id") Long id, @Valid @RequestBody Comment commentForm) {

        if (commentForm.getNickname() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "닉네임 입력 필수");
        }

        Answer answer = answerService.getAnswer(id);
        if (answer == null) return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        Comment comment = commentService.create(answer, commentForm);
        if (comment == null) return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        return (comment != null) ? ResponseEntity.status(HttpStatus.OK).body(comment) : ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    // 대댓글 수정 api
    @PutMapping("/comments/{id}")
    public ResponseEntity<Comment> answerModify(@Valid @RequestBody Comment newComment, @PathVariable("id") Long id) {

        if (newComment.getPassword().equals(newComment.getPassword())) {

        return commentRepository.findById(id)
                .map(comment -> {
                    comment.setContent(newComment.getContent());
                    commentRepository.save(comment);
                    return ResponseEntity.status(HttpStatus.OK).body(comment);
                })
                .orElseGet(() -> {
                    newComment.setId(id);
                    commentRepository.save(newComment);
                    return ResponseEntity.status(HttpStatus.OK).body(newComment);
                });

        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "비밀번호 불일치");
        }
    }

    // 대댓글 삭제 API
    @DeleteMapping("/comments/{id}")
    public ResponseEntity deleteComment(@PathVariable("id") Long id, String password) {
        Comment comment = this.commentService.getComment(id).orElse(null);
        if (comment == null) return new ResponseEntity(HttpStatus.NOT_FOUND);

        if (password == null || password.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "비밀번호 입력 필수");
        }

        if (password.equals(comment.getPassword())) {
            this.commentService.delete(comment);
            return ResponseEntity.ok("정상적으로 삭제되었습니다");
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "비밀번호 불일치");
        }
    }
}
