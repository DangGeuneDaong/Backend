package com.dgd.exception.error;

import com.dgd.exception.message.ChatErrorMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ChatException extends RuntimeException{
    private final ChatErrorMessage chatErrorMessage;
}
