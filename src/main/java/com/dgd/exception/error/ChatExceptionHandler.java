package com.dgd.exception.error;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ChatExceptionHandler {


    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<ChatErrorResponse> handleApplicationException(ChatException e){
        return ChatErrorResponse.toResponseEntity(e.getChatErrorMessage());
    }

}

