package com.example.board.api;

import com.example.board.form.CommentForm;
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
    public Comment one(@PathVariable Long id) {

        return commentRepository.findById(id).orElse(null);
    }

    // 대댓글 등록 API
    @PostMapping(value = "/comments/{id}")
    public ResponseEntity<Comment> createBoardComment(@PathVariable("id") Long id, @RequestBody CommentForm commentForm) {
        Answer answer = answerService.getAnswer(id);
        Comment comment = commentService.create(answer, commentForm.getContent());

        return (comment != null) ? ResponseEntity.status(HttpStatus.OK).body(comment) : ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    // 대댓글 수정 api
    @PutMapping("/comments/{id}")
    public Comment answerModify(@RequestBody Comment newComment, @PathVariable("id") Long id) {
        return commentRepository.findById(id)
                .map(comment -> {
                    comment.setContent(newComment.getContent());
                    return commentRepository.save(comment);
                })
                .orElseGet(() -> {
                    newComment.setId(id);
                    return commentRepository.save(newComment);
                });
    }

    // 대댓글 삭제 API
    @DeleteMapping("/comments/{id}")
    public void deleteComment(@PathVariable("id") Long id) {
        Optional<Comment> comment = this.commentService.getComment(id);
        if (comment.isPresent()) {
            Comment c = comment.get();
            this.commentService.delete(c);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "entity not found");
        }
    }
}
