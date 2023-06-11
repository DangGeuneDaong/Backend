package com.dgd.model.entity;

import com.dgd.model.type.PetGender;
import com.dgd.model.type.PetSize;
import com.dgd.model.type.PetType;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
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
    private int petAge;
    @Enumerated(EnumType.STRING)
    private PetGender petGender;
    @Enumerated(EnumType.STRING)
    private PetSize petSize;
    private String petProfileUrl;
    @Column(nullable = false)
    private Long userId;
}
