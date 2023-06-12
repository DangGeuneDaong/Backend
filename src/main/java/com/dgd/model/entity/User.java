package com.dgd.model.entity;

import com.dgd.model.type.Role;
import com.dgd.model.type.SocialType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
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
    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "userId")
    private List<Pet> petList;
    @Enumerated(EnumType.STRING)
    private Role role;
    @Enumerated(EnumType.STRING)
    private SocialType socialType; // KAKAO, NAVER
    private String socialId; // 소셜 아이디 ( 기본 로그인은 null )
    private String token;
    public void authorizeUser() { // 유저 권한 설정
        this.role = Role.USER;
    }

    public void addPet(Pet pet) {
        petList.add(pet);
    }

    public void passwordEncode(PasswordEncoder passwordEncoder) {
        this.password = passwordEncoder.encode(this.password);
    }

    public void updateRefreshToken(String updateRefreshToken) {
        this.token = updateRefreshToken;
    }
}
