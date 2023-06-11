package com.dgd.exception.error;


import com.dgd.exception.type.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthenticationException extends RuntimeException{
    private final ErrorCode errorCode;
}
