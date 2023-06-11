package com.dgd.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String userId;
    @Column(nullable = false)
    private String nickName;
    @Column(nullable = false)
    private String password;
    private String location; // DB 저장용 지역 이름
    private String latitude; // 위도
    private String longitude; // 경도
    private String profileUrl;
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "userId")
    @JsonIgnore
    private List<Pet> petList;

    public void addPet(Pet pet) {
        petList.add(pet);
    }
}
