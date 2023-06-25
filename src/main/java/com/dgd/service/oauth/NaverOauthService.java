package com.dgd.service.oauth;

import com.dgd.config.CookieProvider;
import com.dgd.config.JwtTokenProvider;
import com.dgd.exception.error.AuthenticationException;
import com.dgd.model.dto.UserSignInDto;
import com.dgd.model.entity.User;
import com.dgd.model.repo.UserRepository;
import com.dgd.model.type.Role;
import com.dgd.model.type.SocialType;
import com.dgd.service.UserService;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.SecureRandom;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class NaverOauthService {
    private final String state = "daenggeun";

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final CookieProvider cookieProvider;
    private final RedisTemplate<String, String> redisTemplate;
    private final UserService userService;
    private final Long refreshTokenValidTime = 2 * 24 * 60 * 60 * 1000L;


    public String createNaverUser(String token, HttpServletResponse response) throws AuthenticationException {


        String header = "Bearer " + token; // Bearer 다음에 공백 추가
        try {
            String apiURL = "https://openapi.naver.com/v1/nid/me";
            URL url = new URL(apiURL);
            HttpURLConnection con = (HttpURLConnection)url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("Authorization", header);
            int responseCode = con.getResponseCode();
            BufferedReader br;
            if(responseCode == 200) { // 정상 호출
                br = new BufferedReader(new InputStreamReader(con.getInputStream()));
            } else {  // 에러 발생
                br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
            }
            String inputLine;
            StringBuffer bufferResponse = new StringBuffer();
            while ((inputLine = br.readLine()) != null) {
                bufferResponse.append(inputLine);
            }

            JsonParser parser = new JsonParser();
            JsonElement element = parser.parse(bufferResponse.toString());

            JsonElement responseVal = element.getAsJsonObject().get("response");
            String userId = responseVal.getAsJsonObject().get("id").getAsString();
            String email = responseVal.getAsJsonObject().get("email").getAsString();
            String profileUrl = responseVal.getAsJsonObject().get("profile_image").getAsString();
            /**
             * TODO
             * 프로필URL 추가
             */

            if (userRepository.findBySocialTypeAndSocialId(SocialType.NAVER, userId).isEmpty()) {
                saveUser(String.valueOf(userId), email, profileUrl);


                Authentication authentication = authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(userId, passwordEncoder.encode(userId)));

                String accessToken = jwtTokenProvider.generateAccessToken(authentication);
                String refreshToken = jwtTokenProvider.generateRefreshToken(authentication);

                br.close();

                cookieProvider.setRefreshTokenCookie(refreshToken, response);

                redisTemplate.opsForValue().set(
                        authentication.getName(),
                        refreshToken,
                        refreshTokenValidTime,
                        TimeUnit.MILLISECONDS);

                br.close();


                return accessToken;
            } else if (userRepository.findBySocialTypeAndSocialId(SocialType.NAVER, email).isPresent()){
                UserSignInDto dto = UserSignInDto.builder()
                        .userId(String.valueOf(email))
                        .password(passwordEncoder.encode(String.valueOf(email)))
                        .build();

                br.close();

                return userService.signIn(dto, response);
            }

        } catch (Exception e) {
            System.out.println(e);
        }
        return null;
    }

    public User saveUser(String userId, String email, String profileUrl) {
        User user = User.builder()
                .userId(email)
                .password(email.toString())
                .socialId(userId)
                .socialType(SocialType.NAVER)
                .profileUrl(profileUrl)
                .role(Role.GUEST)
                .build();
        return userRepository.save(user);
    }
}
