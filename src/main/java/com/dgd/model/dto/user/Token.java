package com.dgd.model.dto.user;

import lombok.Getter;

@Getter
public class Token {
    private String token;
    private Long validTime;

    public Token(String token, Long validTime) {
        this.token = token;
        this.validTime = validTime;
    }
}
