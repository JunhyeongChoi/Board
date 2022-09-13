package com.example.board.config;

import com.example.board.service.UserSecurityService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.header.writers.frameoptions.XFrameOptionsHeaderWriter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final UserSecurityService userSecurityService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf().disable();

        http
                .authorizeRequests()
                .antMatchers("/**")
                .permitAll()

//                .and().csrf().ignoringAntMatchers("/h2-console/**")
                .and()
                .headers()
                .addHeaderWriter(new XFrameOptionsHeaderWriter(
                        XFrameOptionsHeaderWriter.XFrameOptionsMode.SAMEORIGIN))

                // 스프링 시큐리티의 로그인 설정을 담당하는 부분으로 로그인 페이지의 URL은 /user/login이고
                // 로그인 성공시에 이동하는 디폴트 페이지는 루트 URL(/)임을 의미
                .and()
                .formLogin()
                .usernameParameter("username")
                .passwordParameter("password")
                .defaultSuccessUrl("/")

//     로그아웃을 위한 설정을 추가했다. 로그아웃 URL을 /user/logout으로 설정하고 로그아웃이 성공하면 루트(/) 페이지로 이동하도록 했다.
//     그리고 로그아웃시 생성된 사용자 세션도 삭제하도록 처리했다.
                .and()
                .logout()
                .logoutRequestMatcher(new AntPathRequestMatcher("/user/logout"))
                .logoutSuccessUrl("/")
                .invalidateHttpSession(true)
        ;

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /*
     * AuthenticationManager 빈을 생성했다. AuthenticationManager 는 스프링 시큐리티의 인증을 담당한다.
     * AuthenticationManager 빈 생성시 스프링의 내부 동작으로 인해
     * 작성한 UserSecurityService 와 PasswordEncoder 가 자동으로 설정된다.
     * */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}