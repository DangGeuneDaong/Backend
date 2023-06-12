package com.dgd.model.entity;

import com.dgd.model.type.PetGender;
import com.dgd.model.type.PetSize;
import com.dgd.model.type.PetType;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    private PetType petType;
    @Column(nullable = false)
    private String petName;
    @Column(nullable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date petAge;
    @Enumerated(EnumType.STRING)
    private PetGender petGender;
    @Enumerated(EnumType.STRING)
    private PetSize petSize;
    @Column(nullable = false)
    private Long userId;
}
