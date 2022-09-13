package com.example.board.service;

import com.example.board.DataNotFoundException;
import com.example.board.model.SiteUser;
import com.example.board.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public SiteUser create(String username, String email, String password) {

        SiteUser user = new SiteUser();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        this.userRepository.save(user);
        return user;

    }

    // getUser 는 id 로 엔티티를 찾는 다른 함수와 다르게 사용자의 이름으로 엔티티를 찾아낸다
    public SiteUser getUser(String username) {
        Optional<SiteUser> siteUser = userRepository.findByusername(username);
        if (siteUser.isPresent()) {
            return siteUser.get();
        } else {
            throw new DataNotFoundException("등록되지 않은 사용자입니다.");
        }
    }


}