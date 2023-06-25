package com.dgd.service;

import com.dgd.config.CookieProvider;
import com.dgd.exception.error.AuthenticationException;
import com.dgd.model.dto.Point;
import com.dgd.model.dto.UpdateUserDto;
import com.dgd.model.dto.UserSignInDto;
import com.dgd.model.dto.UserSignUpDto;
import com.dgd.model.entity.User;
import com.dgd.model.repo.UserRepository;
import com.dgd.config.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static com.dgd.exception.message.AuthErrorMessage.*;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final RedisTemplate<String, String> redisTemplate;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;
    private final CookieProvider cookieProvider;
    private final MapService mapService;

    private final Long refreshTokenValidTime = 2 * 24 * 60 * 60 * 1000L;


    public User signUp (UserSignUpDto signUpDto) {
        if (userRepository.findByUserId(signUpDto.getUserId()).isPresent()) {
            throw new AuthenticationException(ALREADY_REGISTERED);
        }

        if (userRepository.findByNickName(signUpDto.getNickName()).isPresent()) {
            throw new AuthenticationException(DUPLICATED_NICKNAME);
        }
        Point point = mapService.getMapString(signUpDto.getLocation());


        User user = User.builder()
                .nickName(signUpDto.getNickName())
                .userId(signUpDto.getUserId())
                .password(passwordEncoder.encode(signUpDto.getPassword()))
                .location(signUpDto.getLocation())
                .latitude(point.getLatitude())
                .longitude(point.getLongitude())
                .build();

        user.authorizeUser();

        return userRepository.save(user);
    }

    public String signIn (UserSignInDto signInDto, HttpServletResponse response) {
        User user = userRepository.findByUserId(signInDto.getUserId())
                .orElseThrow(() -> new AuthenticationException(USER_NOT_FOUND));

        if(!passwordEncoder.matches(signInDto.getPassword(), user.getPassword())) {
            throw new AuthenticationException(MISMATCH_PASSWORD);
        }

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(signInDto.getUserId(), signInDto.getPassword()));
        /**
         * TODO
         * 프로필 URL
         */

        String accessToken = jwtTokenProvider.generateAccessToken(authentication);
        String refreshToken = jwtTokenProvider.generateRefreshToken(authentication);
        cookieProvider.setRefreshTokenCookie(refreshToken, response);

        redisTemplate.opsForValue().set(
                authentication.getName(),
                refreshToken,
                refreshTokenValidTime,
                TimeUnit.MILLISECONDS);

        return accessToken;
    }

    public String findSocialUserNickName(String socialId) { // 소셜 로그인한 사람의 소셜 이메일로 DB에서 유저의 닉네임을 검색
        String userNickName = userRepository.findBySocialId(socialId)
                .orElseThrow(() -> new AuthenticationException(USER_NOT_FOUND)).getNickName();
        return userNickName;
    }

    /**
     * TODO
     * MultiPartFile 추가해서 프로필 사진 추가하기
     */
    public User updateUser(UpdateUserDto updateUserDto) {
        Point point = mapService.getMapString(updateUserDto.getLocation());
        double latitude = point.getLatitude();
        double longitude = point.getLongitude();

        User user = userRepository.findById(updateUserDto.getId())
               .orElseThrow(() -> new AuthenticationException(USER_NOT_FOUND));

        user.setLatAndLon(latitude, longitude);
        user.update(updateUserDto);
        user.authorizeUser();

       return user;
    }

    public String getNewAccessToken (String userId) {
       String redisVal = redisTemplate.opsForValue().get(userId); // redis 에 userId 가 key 값인 refresh token

        if (jwtTokenProvider.validateRefreshToken(redisVal)) {
            String userPassword = userRepository.findByUserId(userId).get().getPassword();
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(userId, userPassword));

            return jwtTokenProvider.generateAccessToken(authentication);
        } else {
            throw new AuthenticationException(INVALID_TOKEN);
        }
    }

    public void logout (String accessToken) {
        if (jwtTokenProvider.validateAccessToken(accessToken)) {
            throw new AuthenticationException(INVALID_TOKEN);
        }

        String userId = jwtTokenProvider.getPayloadSub(accessToken);
        String refreshToken = redisTemplate.opsForValue().get(userId);

        if (refreshToken != null) {
            redisTemplate.delete(refreshToken);
            redisTemplate.opsForValue().set(accessToken, "blacklist", jwtTokenProvider.getPayloadExp(accessToken));
        }
    }
}
