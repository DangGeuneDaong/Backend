package com.dgd.exception.type;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    USER_NOT_FOUND(HttpStatus.BAD_REQUEST, "찾을 수 없는 회원입니다."),
    MISMATCH_PASSWORD(HttpStatus.BAD_REQUEST, "비밀번호가 일치하지 않습니다."),
    UNAUTHORIZED_USER(HttpStatus.BAD_GATEWAY, "로그인이 필요합니다."),
    INVALID_TOKEN(HttpStatus.BAD_REQUEST, "인가되지 않은 토큰입니다.")
    ;

    private final HttpStatus status;
    private final String errorMessage;

    ErrorCode(HttpStatus status, String errorMessage) {
        this.status = status;
        this.errorMessage = errorMessage;
    }
}
