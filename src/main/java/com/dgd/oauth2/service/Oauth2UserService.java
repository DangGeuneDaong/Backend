package com.dgd.oauth2.service;

import com.dgd.model.entity.User;
import com.dgd.model.type.SocialType;
import com.dgd.oauth2.model.Oauth2User;
import com.dgd.oauth2.model.OauthAttribute;
import com.dgd.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class Oauth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
    private final UserRepository userRepository;

    private static final String NAVER = "naver";
    private static final String KAKAO = "kakao";
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        log.info("Oauth2UserService.loadUser() 실행 - OAuth2 로그인 요청");
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oauth2User = delegate.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        SocialType socialType = getSocialType(registrationId);
        String userNameAttributeName = userRequest.getClientRegistration()
                .getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName(); // OAuth2 로그인 시 키(PK)가 되는 값
        Map<String, Object> attribute = oauth2User.getAttributes();
        // socialType에 따라 유저 정보를 통해 OAuthAttributes 객체 생성
        OauthAttribute extractAttribute = OauthAttribute.of(socialType, userNameAttributeName, attribute);

        User createdUser = getUser(extractAttribute, socialType); // getUser() 메소드로 User 객체 생성 후 반환

        return new Oauth2User(
                Collections.singleton(new SimpleGrantedAuthority(createdUser.getRole().getKey())),
                attribute,
                extractAttribute.getNameAttribute(),
                createdUser.getUserId(),
                createdUser.getRole());
    }

    private SocialType getSocialType(String registrationId) {
        if(NAVER.equals(registrationId)) {
            return SocialType.NAVER;
        }
            return SocialType.KAKAO;
    }

    /**
     * SocialType과 attributes에 들어있는 소셜 로그인의 식별값 id를 통해 회원을 찾아 반환하는 메소드
     * 만약 찾은 회원이 있다면, 그대로 반환하고 없다면 saveUser()를 호출하여 회원을 저장한다.
     */
    private User getUser(OauthAttribute attribute, SocialType socialType) {
        User findUser = userRepository.findBySocialTypeAndSocialId(socialType,
                attribute.getUserInfo().getId()).orElse(null);

        if(findUser == null) {
            return saveUser(attribute, socialType);
        }
        return findUser;
    }

    /**
     * OAuthAttributes의 toEntity() 메소드를 통해 빌더로 User 객체 생성 후 반환
     * 생성된 User 객체를 DB에 저장 : socialType, socialId, email, role 값만 있는 상태
     */
    private User saveUser(OauthAttribute attribute, SocialType socialType) {
        User createdUser = attribute.toEntity(socialType, attribute.getUserInfo());
        return userRepository.save(createdUser);
    }
}
