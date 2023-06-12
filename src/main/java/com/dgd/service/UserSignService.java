package com.dgd.service;

import com.dgd.exception.error.AuthenticationException;
import com.dgd.model.dto.user.SignInDto;
import com.dgd.model.dto.user.SignUpDto;
import com.dgd.model.entity.User;
import com.dgd.model.type.Role;
import com.dgd.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.dgd.exception.type.ErrorCode.*;

@Service
@RequiredArgsConstructor
@Transactional
public class UserSignService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * 위치는 지도 입력 후 latitude, longitude 전환 API 이용 후 입력
     * 로그인에 토큰입력 아직 x ( redis 하고 ! )
     */
    public User signUp (SignUpDto signUpDto) {
        if (userRepository.findByUserId(signUpDto.getUserId()).isPresent()) {
            throw new AuthenticationException(ALREADY_REGISTERED);
        }

        if (userRepository.findByNickName(signUpDto.getNickName()).isPresent()) {
            throw new AuthenticationException(DUPLICATED_NICKNAME);
        }
        User user = User.builder()
                .nickName(signUpDto.getNickName())
                .userId(signUpDto.getUserId())
                .password(passwordEncoder.encode(signUpDto.getPassword()))
                .location(signUpDto.getLocation())
                .role(Role.USER)
                .build();

        return userRepository.save(user);
    }

    public void signIn (SignInDto signInDto) {
        User user = userRepository.findByUserId(signInDto.getUserId())
                .orElseThrow(() -> new AuthenticationException(USER_NOT_FOUND));

        if(!user.getPassword().equals(signInDto.getPassword())) {
            throw new AuthenticationException(MISMATCH_PASSWORD);
        }
    }
}
