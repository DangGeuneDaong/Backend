package com.dgd.model.dto.pet;

import com.dgd.model.type.PetGender;
import com.dgd.model.type.PetSize;
import com.dgd.model.type.PetType;
import lombok.*;

import javax.persistence.Column;
import javax.validation.constraints.NotBlank;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PetDto {
    @NotBlank
    private Long userId;
    @NotBlank
    private PetType petType;
    @NotBlank
    private String petName;
    @NotBlank
    private PetGender petGender;
    @NotBlank
    private Date petAge;
    @Column(nullable = false)
    private PetSize petSize;

}
