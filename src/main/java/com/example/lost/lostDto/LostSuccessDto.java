package com.example.lost.lostDto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LostSuccessDto {

    private Boolean success;

    public LostSuccessDto(Boolean success) {
        this.success = success;
    }

}