package com.dgd.exception.error;

import com.dgd.exception.message.ChatErrorMessage;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.ResponseEntity;

@Getter
@Builder
public class ChatErrorResponse {
    private final int status;
    private final String error;
    private final String code;
    private final String message;

    public static ResponseEntity<ChatErrorResponse> toResponseEntity(ChatErrorMessage chatErrorMessage) {
        return ResponseEntity.status(chatErrorMessage.getStatus())
                .body(ChatErrorResponse.builder()
                        .status(chatErrorMessage.getStatus().value())
                        .error(chatErrorMessage.getStatus().name())
                        .code(chatErrorMessage.name())
                        .message(chatErrorMessage.getErrorMessage())
                        .build());
    }
}