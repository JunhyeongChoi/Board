package com.example.question.api;

import com.example.question.dto.SuccessDto;
import com.example.question.entity.Question;
import com.example.question.dto.AnswerDto;
import com.example.question.dto.QuestionDto;
import com.example.question.dto.CommentDto;
import com.example.question.entity.Answer;
import com.example.question.entity.Comment;
import com.example.question.repository.QuestionRepository;
import com.example.question.service.AnswerService;
import com.example.question.service.QuestionService;
import com.example.question.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.io.File;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/lost")
class QuestionApiController {

    @Autowired
    private QuestionRepository repository;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private AnswerService answerService;

    @Autowired
    private CommentService commentService;

    // 페이징, 검색(제목, 내용에 포함) 조회 API
    @GetMapping("/posts")
    public Page<Question> list(@PageableDefault(size = 10, sort = {"id"}, direction = Sort.Direction.DESC) Pageable pageable,
                               @RequestParam(required = false, defaultValue = "") String searchText) {

        return repository.findBySubjectContainingOrContentContaining(searchText, searchText, pageable);
    }

    /*
    * 상세 질문을 보기 위해 데이터를 가공하는 함수
      답변 페이징 처리를 위해 @RequestParam(value = "page", defaultValue = "0") int page 추가
    * */
    @GetMapping("/posts/{id}")
    public ResponseEntity<Map<String, Object>> detail(@PathVariable("id") Long id,
                                                      @RequestParam(value = "page", defaultValue = "0") int page) {

        // 답변 페이징 처리
        Page<Answer> pagingAnswer = answerService.getList(page, id);
        Question question = this.questionService.getQuestion(id);
        Page<Comment> commentPage = commentService.getQuestionCommentList(page, id);

        if (pagingAnswer.getNumberOfElements() == 0 && page != 0) {
            throw new IllegalArgumentException("잘못된 입력 값입니다.");
        }

        QuestionDto questionDto = new QuestionDto(question.getId(), question.getSubject(), question.getContent(),
                question.getCreateDate(), question.getUsername(), question.getIsLost());
        
        Page<AnswerDto> answerPagingDto = pagingAnswer.map(
                post -> new AnswerDto(
                        post.getId(),post.getContent(),post.getCreateDate(),
                        post.getUsername(),
                        post.getCommentList()
                ));
        Page<CommentDto> commentDtoPage = commentPage.map(
                post -> new CommentDto (
                        post.getId(),post.getContent(),post.getCreateDate(),
                        post.getUsername()
                ));

        Map<String, Object> result = new HashMap<>();
        result.put("postComments", commentDtoPage);
        result.put("posts", questionDto);
        result.put("answers", answerPagingDto);

        return ResponseEntity.ok(result);
    }

    // 글 작성 API
    @PostMapping("/posts")
    Question newQuestion(@Valid Question newQuestion, MultipartFile file, BindingResult bindingResult) throws Exception {

        if (bindingResult.hasErrors()) {
            throw new IllegalArgumentException("잘못된 입력 값입니다.");
        }

        if (newQuestion.getUsername() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "닉네임 입력 필수");
        }

        LocalDateTime now = LocalDateTime.now();
        newQuestion.setCreateDate(now);

        if (file == null) {
            repository.save(newQuestion);
        } else {
            questionService.write(newQuestion, file);
        }

        return repository.findById(newQuestion.getId()).orElse(null);
    }

    // 글 수정 API
    @PutMapping("/posts/{id}")
    ResponseEntity<Question> replaceQuestion(@Valid Question newQuestion, @PathVariable Long id, MultipartFile file, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            throw new IllegalArgumentException("잘못된 입력 값입니다.");
        }

        Question exQuestion = repository.findById(id).orElse(null);
        if (exQuestion == null) return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        // 새로운 파일경로
        String filePath = questionService.getFilePath(newQuestion);

        // 삭제할 파일경로
        String deleteFilePath = questionService.getFilePath(exQuestion);

        if (newQuestion.getPassword().equals(exQuestion.getPassword())) {

            return repository.findById(id)
                    .map(question -> {
                        question.setSubject(newQuestion.getSubject());
                        question.setContent(newQuestion.getContent());
                        question.setIsLost(newQuestion.getIsLost());

                        if (file == null) {
                            questionService.deleteFile(question);
                            repository.save(question);
                            return ResponseEntity.status(HttpStatus.OK).body(question);
                        }

                        if (file.isEmpty()) {
                            questionService.deleteFile(question);
                            repository.save(question);
                            return ResponseEntity.status(HttpStatus.OK).body(question);
                        } else {
                            try {
                                // 파일을 수정할 경우 기존 파일 제거
                                if (!filePath.equals(deleteFilePath)) {
                                    File deleteFile = new File(deleteFilePath);
                                    deleteFile.delete();
                                }

                                questionService.write(question, file);
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        }

                        Question resultQuestion = repository.findById(question.getId()).orElse(null);
                        return ResponseEntity.status(HttpStatus.OK).body(resultQuestion);
                    })
                    .orElseGet(() -> {
                        newQuestion.setId(id);
                        repository.save(newQuestion);
                        return ResponseEntity.status(HttpStatus.OK).body(newQuestion);
                    });
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다");
        }
    }

//    // 파일 삭제 API
//    @DeleteMapping("/questions/files/{id}")
//    void deleteFile(@PathVariable Long id) {
//
//        Question question = repository.findById(id).orElse(null);
//
//        questionService.deleteFile(question);
//        repository.save(question);
//    }

    // 글 삭제 API
    @DeleteMapping("/posts/{id}")
    ResponseEntity deleteQuestion(@PathVariable Long id, String password) {

        Question question = repository.findById(id).orElse(null);

        if (question == null) return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        if (password == null || password.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "비밀번호 입력 필수");
        }

        if (password.equals(question.getPassword())) {
            SuccessDto successDto = new SuccessDto(this.questionService.delete(question));
            questionService.deleteFile(question);
            return ResponseEntity.ok(successDto);

        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제 권한이 없습니다.");
        }
    }

}