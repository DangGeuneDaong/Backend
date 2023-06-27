package com.dgd.exception.message;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ChatErrorMessage {

    UNMATCHED_USER(HttpStatus.BAD_REQUEST, "입장할 수 없는 사용자입니다."),
    ALREADY_CLOSED(HttpStatus.BAD_REQUEST, "이미 종료된 채팅입니다"),
    CHATROOM_NOT_FOUND(HttpStatus.BAD_REQUEST, "채팅방을 찾을 수 없습니다."),
    NOT_ACTIVATED_APPLICATION(HttpStatus.BAD_REQUEST, "찾을 수 없는 나눔 신청입니다.")
    ;

    private final HttpStatus status;
    private final String errorMessage;

    ChatErrorMessage(HttpStatus status, String errorMessage) {
        this.status = status;
        this.errorMessage = errorMessage;
    }
}
