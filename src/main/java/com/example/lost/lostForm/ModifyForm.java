package com.example.lost.lostForm;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ModifyForm {

    private String content;
    private String createDate;

    public ModifyForm(String content, String createDate) {

        this.content = content;
        this.createDate = createDate;
    }
}