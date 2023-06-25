package com.dgd.exception.error;

import com.dgd.exception.message.ApplicationErrorCode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;



@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationErrorResponse {

    private HttpStatus status;
    private String code;
    private int errorCode;
    private String description;
    public static ResponseEntity<ApplicationErrorResponse> toResponseEntity(ApplicationErrorCode errorCode){
        return ResponseEntity.status(errorCode.getStatus())
                .body(
                  ApplicationErrorResponse.builder()
                          .status(errorCode.getStatus())
                          .code(errorCode.name())
                          .errorCode(errorCode.getErrorCode())
                          .description(errorCode.getDescription())
                          .build()
                );
    }
}