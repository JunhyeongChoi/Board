package com.example.board.service;

import com.example.board.DataNotFoundException;
import com.example.board.model.Board;
import com.example.board.repository.BoardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.Optional;
import java.util.UUID;

@Service
public class BoardService {

    @Autowired
    private BoardRepository boardRepository;

    // 글 등록
    public void write(Board board, MultipartFile file) throws Exception {

        if (!file.getOriginalFilename().isEmpty()) {

            String projectPath = System.getProperty("user.dir") + "\\src\\main\\resources\\static\\files";

            UUID uuid = UUID.randomUUID();

            String fileName = uuid + "_" + file.getOriginalFilename();

            File saveFile = new File(projectPath, fileName);

            file.transferTo(saveFile);

            board.setFilename(fileName);
            board.setFilepath("/files/" + fileName);

        }

        boardRepository.save(board);

    }

    // 파일 삭제
    public void deleteFile(Board board) {

        // 파일의 경로 + 파일명
        String filePath = System.getProperty("user.dir") + "\\src\\main\\resources\\static\\files\\" + board.getFilename();
        File deleteFile = new File(filePath);

        // 파일이 존재하는지 체크 존재할경우 true, 존재하지않을경우 false
        if(deleteFile.exists()) {

            // 파일을 삭제합니다.
            deleteFile.delete();
        }

        board.setFilename(null);
        board.setFilepath(null);
    }

    public String getFilePath(Board board) {
        String filePath = System.getProperty("user.dir") + "\\src\\main\\resources\\static\\files\\" + board.getFilename();

        return filePath;
    }

    public Board getBoard(Long id) {
        Optional<Board> board = this.boardRepository.findById(id);
        if (board.isPresent()) {
            return board.get();
        } else {
            throw new DataNotFoundException("question not found");
        }
    }

}
