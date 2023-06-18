package com.dgd.exception;

import lombok.Getter;
@Getter
public class ApplicationException extends RuntimeException {

    private ApplicationErrorCode errorCode;
    private String description;

    public ApplicationException(ApplicationErrorCode errorCode){
        this.errorCode = errorCode;
        this.description = errorCode.getDescription();
    }
}