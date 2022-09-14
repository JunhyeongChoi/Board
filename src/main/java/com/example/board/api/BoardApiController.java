package com.example.board.api;

import com.example.board.repository.BoardRepository;
import com.example.board.model.Board;
import com.example.board.service.BoardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
    Board newBoard(Board newBoard, MultipartFile file) throws Exception {

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
    Board one(@PathVariable Long id) {

        return repository.findById(id).orElse(null);
    }

    // 글 수정 API
    @PutMapping("/boards/{id}")
    Board replaceBoard(Board newBoard, @PathVariable Long id, MultipartFile file) {

        return repository.findById(id)
                .map(board -> {
                    board.setTitle(newBoard.getTitle());
                    board.setContent(newBoard.getContent());
                    board.setIsLost(newBoard.getIsLost());

                    if (file == null) {
                        boardService.deleteFile(board);
                        return repository.save(board);
                    } else {
                        try {
                            boardService.write(board, file);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }

                    Board resultBoard = repository.findById(board.getId()).orElse(null);
                    return resultBoard;
                })
                .orElseGet(() -> {
                    newBoard.setId(id);
                    return repository.save(newBoard);
                });
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
    void deleteBoard(@PathVariable Long id) {

        Board board = repository.findById(id).orElse(null);

        boardService.deleteFile(board);
        repository.deleteById(id);
    }

}