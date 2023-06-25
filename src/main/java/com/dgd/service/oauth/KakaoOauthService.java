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
import com.google.gson.JsonParser;
import com.google.gson.JsonElement;
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
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class KakaoOauthService {
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final CookieProvider cookieProvider;
    private final RedisTemplate<String, String> redisTemplate;
    private final UserService userService;
    private final Long refreshTokenValidTime = 2 * 24 * 60 * 60 * 1000L;

    @Value("${spring.security.oauth2.client.registration.kakao.client-authentication-method}")
    private String kakaoClientAuthenticationMethod;

    public String createKakaoUser(String token, HttpServletResponse response) throws AuthenticationException {

        String reqURL = "https://kapi.kakao.com/v2/user/me";

        try {
            URL url = new URL(reqURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod(kakaoClientAuthenticationMethod);
            conn.setDoOutput(true);
            conn.setRequestProperty("Authorization", "Bearer " + token); //전송할 header 작성, access_token전송

            //결과 코드가 200이라면 성공
            int responseCode = conn.getResponseCode();
            System.out.println("responseCode : " + responseCode);

            //요청을 통해 얻은 JSON타입의 Response 메세지 읽어오기
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line = "";
            String result = "";

            while ((line = br.readLine()) != null) {
                result += line;
            }
            System.out.println("response body : " + result);

            //Gson 라이브러리로 JSON파싱
            JsonParser parser = new JsonParser();
            JsonElement element = parser.parse(result);

            BigInteger id = element.getAsJsonObject().get("id").getAsBigInteger();
            boolean hasEmail = element.getAsJsonObject().get("kakao_account").getAsJsonObject().get("has_email").getAsBoolean();
            String profileUrl = element.getAsJsonObject().get("properties").getAsJsonObject().get("profile_image").getAsString();
            String nickName = "";
            if(hasEmail){
                nickName = element.getAsJsonObject().get("properties").getAsJsonObject().get("nickname").getAsString();
            }

            System.out.println("id : " + id);
            System.out.println("nickName : " + nickName);
            /**
             * TODO
             * 프로필URL 추가
             */

            if (userRepository.findBySocialTypeAndSocialId(SocialType.KAKAO, nickName).isEmpty()) {
                saveUser(String.valueOf(id), nickName, profileUrl);


                Authentication authentication = authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(id, passwordEncoder.encode(id.toString())));

                String accessToken = jwtTokenProvider.generateAccessToken(authentication);
                String refreshToken = jwtTokenProvider.generateRefreshToken(authentication);

                br.close();

                cookieProvider.setRefreshTokenCookie(refreshToken, response);

                redisTemplate.opsForValue().set(
                        authentication.getName(),
                        refreshToken,
                        refreshTokenValidTime,
                        TimeUnit.MILLISECONDS);

                return accessToken;
            } else if (userRepository.findBySocialTypeAndSocialId(SocialType.KAKAO, nickName).isPresent()){
                UserSignInDto dto = UserSignInDto.builder()
                                                .userId(String.valueOf(id))
                                                .password(passwordEncoder.encode(String.valueOf(id)))
                                                .build();
                br.close();

                return userService.signIn(dto, response);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void saveUser(String id, String nickName, String profileUrl) {
        User user = User.builder()
                .userId(id)
                .password(passwordEncoder.encode(id))
                .socialId(nickName)
                .socialType(SocialType.KAKAO)
                .profileUrl(profileUrl)
                .role(Role.GUEST)
                .build();
        userRepository.save(user);
    }
}