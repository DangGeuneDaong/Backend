package com.dgd.config;

import com.dgd.oauth2.handler.Oauth2LoginFailureHandler;
import com.dgd.oauth2.handler.Oauth2LoginSuccessHandler;
import com.dgd.oauth2.service.Oauth2UserService;
import com.dgd.repository.UserRepository;
import com.dgd.security.LoginFailureHandler;
import com.dgd.security.LoginSuccessHandler;
import com.dgd.security.UserDetailService;
import com.dgd.security.jwt.JwtAuthenticationFilter;
import com.dgd.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class WebSecurityConfig {
    private final JwtTokenProvider jwtTokenProvider;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final UserDetailService detailService;
    private final UserRepository userRepository;
    private final Oauth2UserService oauth2UserService;
    private final Oauth2LoginSuccessHandler oauth2LoginSuccessHandler;
    private final Oauth2LoginFailureHandler oauth2LoginFailureHandler;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .httpBasic().disable()
                .formLogin().disable()
                .csrf().disable()
                // Spring Security 세션 정책 : 세션을 생성 및 사용하지 않음
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                // 조건별로 요청 허용/제한 설정
                .authorizeRequests()
                .antMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-resources/**").permitAll()
                .antMatchers("/user/**", "/pet/**", "/token/**", "/oauth2/**").permitAll()
                .antMatchers("/signup/**", "/signin/**").permitAll()
                .anyRequest().authenticated();
        http
                .oauth2Login().authorizationEndpoint().baseUri("/oauth2/authorize")
                .and()
                .userInfoEndpoint().userService(oauth2UserService) // customUserService 설정
                .and()
                .successHandler(oauth2LoginSuccessHandler) // 동의하고 계속하기를 눌렀을 때 Handler 설정
                .failureHandler(oauth2LoginFailureHandler); // 소셜 로그인 실패 시 핸들러 설정

                // JWT 인증 필터 적용
                http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(passwordEncoder());
        provider.setUserDetailsService(detailService);
        return new ProviderManager(provider);
    }

    @Bean
    public LoginSuccessHandler loginSuccessHandler() {
        return new LoginSuccessHandler(jwtTokenProvider, userRepository);
    }

    @Bean
    public LoginFailureHandler loginFailureHandler() {
        return new LoginFailureHandler();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public ClientRegistrationRepository clientRegistrationRepository() {
        ClientRegistration kakaoRegistration = ClientRegistration.withRegistrationId("kakao")
                .clientId("4dfd6b1d5759cfcba111821bb7baa29b") //kakao-client-id
                .clientSecret("zzadtKvHdMpBaeKPxghiZbsj7skD5BWy") // kakao-client-secret
                .redirectUri("http://localhost:8081/oauth2/social/kakao") // kakao-redirect-uri
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .authorizationUri("https://kauth.kakao.com/oauth/authorize") // authorization-uri
                .tokenUri("https://kauth.kakao.com/oauth/token") // token-uri
                .build();

        ClientRegistration naverRegistration = ClientRegistration.withRegistrationId("naver")
                .clientId("pDmo4fsVGFPMMxse8cVP") // naver-client-id
                .clientSecret("G5JAg5bt0M") // naver-client-secret
                .redirectUri("http://localhost:8081/oauth2/social/naver") // naver-redirect-uri
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .authorizationUri("https://nid.naver.com/oauth2.0/authorize") // authorization-uri
                .tokenUri("https://nid.naver.com/oauth2.0/token") // token-uri
                .build();

        return new InMemoryClientRegistrationRepository(kakaoRegistration, naverRegistration);
    }

}
