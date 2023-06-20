package com.dgd.model.dto;


import lombok.*;

import javax.persistence.Column;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserSignUpDto {
    @NotBlank
    @Column(unique = true)
    private String nickName;
    @NotBlank
    @Column(unique = true)
    private String userId;
    @NotBlank
    private String password;
    private String location;
}

