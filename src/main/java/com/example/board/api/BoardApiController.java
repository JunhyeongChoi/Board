package com.example.board.api;

import com.example.board.repository.BoardRepository;
import com.example.board.model.Board;
import com.example.board.service.BoardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api")
class BoardApiController {

    @Autowired
    private BoardRepository repository;

    @Autowired
    private BoardService boardService;

    // 페이징, 검색(제목, 내용에 포함) 조회 API
    @GetMapping("/boards")
    public Page<Board> list(@PageableDefault(size = 10, sort = {"id"}, direction = Sort.Direction.DESC) Pageable pageable,
                            @RequestParam(required = false, defaultValue = "") String searchText) {

        return repository.findByTitleContainingOrContentContaining(searchText, searchText, pageable);
    }

    // 글 작성 API
    @PostMapping("/boards")
    Board newBoard(@Valid Board newBoard, MultipartFile file) throws Exception {

        if (newBoard.getNickname() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "닉네임 입력 필수");
        }

        LocalDateTime now = LocalDateTime.now();
        newBoard.setDate(now);

        if (file == null) {
            repository.save(newBoard);
        } else {
            boardService.write(newBoard, file);
        }

        return repository.findById(newBoard.getId()).orElse(null);
    }

    // id로 글 조회 API
    @GetMapping("/boards/{id}")
    ResponseEntity<Board> one(@PathVariable Long id) {
        Board board = repository.findById(id).orElse(null);
        if (board == null) return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(board, HttpStatus.OK);
    }

    // 글 수정 API
    @PutMapping("/boards/{id}")
    ResponseEntity<Board> replaceBoard(@Valid Board newBoard, @PathVariable Long id, MultipartFile file) {

        Board exBoard = repository.findById(newBoard.getId()).orElse(null);

        if (exBoard == null) return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        if (newBoard.getPassword().equals(exBoard.getPassword())) {

            return repository.findById(id)
                    .map(board -> {
                        board.setTitle(newBoard.getTitle());
                        board.setContent(newBoard.getContent());
                        board.setIsLost(newBoard.getIsLost());

                        if (file == null) {
                            boardService.deleteFile(board);
                            repository.save(board);
                            return ResponseEntity.status(HttpStatus.OK).body(board);
                        }

                        if (file.isEmpty()) {
                            boardService.deleteFile(board);
                            repository.save(board);
                            return ResponseEntity.status(HttpStatus.OK).body(board);
                        } else {
                            try {
                                boardService.write(board, file);
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        }

                        Board resultBoard = repository.findById(board.getId()).orElse(null);
                        return ResponseEntity.status(HttpStatus.OK).body(resultBoard);
                    })
                    .orElseGet(() -> {
                        newBoard.setId(id);
                        repository.save(newBoard);
                        return ResponseEntity.status(HttpStatus.OK).body(newBoard);
                    });
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "비밀번호 불일치");
        }
    }

//    // 파일 삭제 API
//    @DeleteMapping("/boards/files/{id}")
//    void deleteFile(@PathVariable Long id) {
//
//        Board board = repository.findById(id).orElse(null);
//
//        boardService.deleteFile(board);
//        repository.save(board);
//    }

    // 글 삭제 API
    @DeleteMapping("/boards/{id}")
    ResponseEntity deleteBoard(@PathVariable Long id, String password) {

        Board board = repository.findById(id).orElse(null);

        if (board == null) return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        if (password == null || password.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "비밀번호 입력 필수");
        }

        if (password.equals(board.getPassword())) {
            boardService.deleteFile(board);
            repository.deleteById(id);

            return ResponseEntity.ok("정상적으로 삭제되었습니다");
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "비밀번호 불일치");
        }
    }

}