package com.dgd.exception.error;

import com.dgd.exception.type.ErrorCode;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.ResponseEntity;

@Getter
@Builder
public class ErrorResponse {
    private final int status;
    private final String error;
    private final String code;
    private final String message;

    public static ResponseEntity<ErrorResponse> toResponseEntity(ErrorCode errorCode) {
        return ResponseEntity.status(errorCode.getStatus())
                                .body(ErrorResponse.builder()
                                .status(errorCode.getStatus().value())
                                .error(errorCode.getStatus().name())
                                .code(errorCode.name())
                                .message(errorCode.getErrorMessage())
                                .build());
    }
}
