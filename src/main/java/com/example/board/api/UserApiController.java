package com.example.board.api;

import com.example.board.form.UserCreateForm;
import com.example.board.model.Answer;
import com.example.board.model.SiteUser;
import com.example.board.repository.UserRepository;
import com.example.board.service.UserService;
import groovy.util.logging.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api")
public class UserApiController {

    private final UserService userService;
    private final UserRepository userRepository;

    // 전체 유저 조회 API
    @GetMapping("/user")
    public List<SiteUser> all() {

        return userRepository.findAll();
    }

    // id로 유저 한 명 조회 API
    @GetMapping("/user/{id}")
    public SiteUser one(@PathVariable Long id) {

        return userRepository.findById(id).orElse(null);
    }

    // 회원가입 API
    @PostMapping("/user/signup")
    public ResponseEntity signup(@RequestBody UserCreateForm userCreateForm, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            //return "signup_form";
            return ResponseEntity.notFound().build(); // -> 404에러를 반환
        }

        if (!userCreateForm.getPassword1().equals(userCreateForm.getPassword2())) {
            bindingResult.rejectValue("password2", "passwordInCorrect",
                    "2개의 패스워드가 일치하지 않습니다.");
            return ResponseEntity.notFound().build();
        }

        try {
            userService.create(userCreateForm.getUsername(),
                    userCreateForm.getEmail(), userCreateForm.getPassword1());
        }catch(DataIntegrityViolationException e) {
            e.printStackTrace();
            bindingResult.reject("signupFailed", "이미 등록된 사용자입니다.");
            return ResponseEntity.notFound().build();
        }catch(Exception e) {
            e.printStackTrace();
            bindingResult.reject("signupFailed", e.getMessage());
            return ResponseEntity.notFound().build();
        }

        SiteUser siteUser = userRepository.findByusername(userCreateForm.getUsername()).orElse(null);

        return ResponseEntity.ok(siteUser);
    }

    // 로그인 API
    @PostMapping("/user/login")
    public ResponseEntity login() {

        return ResponseEntity.ok(1);
    }
}