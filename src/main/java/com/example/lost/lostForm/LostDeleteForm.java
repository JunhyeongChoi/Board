package com.example.lost.lostForm;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

@Getter
@Setter
@NoArgsConstructor
public class LostDeleteForm {

    @NotEmpty(message = "비밀번호 입력은 필수입니다.")
    private String password;

    public LostDeleteForm(String password) {
        this.password = password;
    }
}
