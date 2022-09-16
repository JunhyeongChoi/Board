package com.example.question.service;

import com.example.question.DataNotFoundException;
import com.example.question.entity.Question;
import com.example.question.repository.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.Optional;
import java.util.UUID;

@Service
public class QuestionService {

    @Autowired
    private QuestionRepository questionRepository;

    // 글 등록
    public void write(Question question, MultipartFile file) throws Exception {

        if (!file.getOriginalFilename().isEmpty()) {

            String projectPath = System.getProperty("user.dir") + "\\src\\main\\resources\\static\\files";

            UUID uuid = UUID.randomUUID();

            String fileName = uuid + "_" + file.getOriginalFilename();

            File saveFile = new File(projectPath, fileName);

            file.transferTo(saveFile);

            question.setFilename(fileName);
            question.setFilepath("/files/" + fileName);

        }

        questionRepository.save(question);

    }

    public Boolean delete(Question question) {

        this.questionRepository.delete(question);
        return true;
    }

    // 파일 삭제
    public void deleteFile(Question question) {

        // 파일의 경로 + 파일명
        String filePath = System.getProperty("user.dir") + "\\src\\main\\resources\\static\\files\\" + question.getFilename();
        File deleteFile = new File(filePath);

        // 파일이 존재하는지 체크 존재할경우 true, 존재하지않을경우 false
        if(deleteFile.exists()) {

            // 파일을 삭제합니다.
            deleteFile.delete();
        }

        question.setFilename(null);
        question.setFilepath(null);
    }

    public String getFilePath(Question question) {
        String filePath = System.getProperty("user.dir") + "\\src\\main\\resources\\static\\files\\" + question.getFilename();

        return filePath;
    }

    public Question getQuestion(Long id) {
        Optional<Question> question = this.questionRepository.findById(id);
        if (question.isPresent()) {
            return question.get();
        } else {
            throw new DataNotFoundException("question not found");
        }
    }

}
