package com.example.board.api;

import com.example.board.model.User;
import com.example.board.repository.BoardRepository;
import com.example.board.model.Board;
import com.example.board.service.BoardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api")
class BoardApiController {

    @Autowired
    private BoardRepository repository;

    @Autowired
    private BoardService boardService;

    // 페이징, 검색(제목, 내용에 포함) API
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
            boardService.save(newBoard, file);
        }

        return repository.findById(newBoard.getId()).orElse(null);
    }

    // id 검색 API
    @GetMapping("/boards/{id}")
    Board one(@PathVariable Long id) {

        return repository.findById(id).orElse(null);
    }

    // 글 수정 API
    @PutMapping("/boards/{id}")
    Board replaceBoard(@RequestBody Board newBoard, @PathVariable Long id, MultipartFile file) {

        return repository.findById(id)
                .map(board -> {
                    board.setTitle(newBoard.getTitle());
                    board.setContent(newBoard.getContent());
                    board.setLost(newBoard.getLost());
                    return repository.save(board);
                })
                .orElseGet(() -> {
                    newBoard.setId(id);
                    return repository.save(newBoard);
                });
    }

    @Secured("ROLE_ADMIN")
    @DeleteMapping("/boards/{id}")
    void deleteBoard(@PathVariable Long id) {
        repository.deleteById(id);
    }

}