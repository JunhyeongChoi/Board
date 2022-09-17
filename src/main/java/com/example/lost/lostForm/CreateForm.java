package com.example.lost.lostForm;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CreateForm {

    private String content;
    private String username;
    private String createDate;

    public CreateForm(String content, String username, String createDate) {
        this.content = content;
        this.username = username;
        this.createDate = createDate;
    }

}