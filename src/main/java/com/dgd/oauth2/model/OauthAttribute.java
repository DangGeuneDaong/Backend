package com.dgd.oauth2.model;

import com.dgd.model.entity.User;
import com.dgd.model.type.Role;
import com.dgd.model.type.SocialType;
import com.dgd.oauth2.generate.GenerateKakaoNickName;
import com.dgd.oauth2.generate.GenerateNaverNickName;
import com.dgd.oauth2.info.KakaoOauth2UserInfo;
import com.dgd.oauth2.info.NaverOauth2UserInfo;
import com.dgd.oauth2.info.Oauth2UserInfo;
import lombok.Builder;
import lombok.Getter;

import java.util.Map;
import java.util.UUID;

@Getter
public class OauthAttribute {
    private String nameAttribute;
    private Oauth2UserInfo userInfo;

    @Builder
    public OauthAttribute(String nameAttributeKey, Oauth2UserInfo oauth2UserInfo) {
        this.nameAttribute = nameAttributeKey;
        this.userInfo = oauth2UserInfo;
    }

    public static OauthAttribute of(SocialType socialType,
                                     String userNameAttributeName, Map<String, Object> attributes) {

        if (socialType == SocialType.KAKAO) {
            return ofKakao(userNameAttributeName, attributes);
        }
            return ofNaver(userNameAttributeName, attributes);
    }

    public static OauthAttribute ofKakao(String userNameAttributeName, Map<String, Object> attributes) {
        return OauthAttribute.builder()
                .nameAttributeKey(userNameAttributeName)
                .oauth2UserInfo(new KakaoOauth2UserInfo(attributes))
                .build();
    }

    public static OauthAttribute ofNaver(String userNameAttributeName, Map<String, Object> attributes) {
        return OauthAttribute.builder()
                .nameAttributeKey(userNameAttributeName)
                .oauth2UserInfo(new NaverOauth2UserInfo(attributes))
                .build();
    }

    public String generateNickName(SocialType socialType) {
        if(socialType == SocialType.KAKAO) {
            return GenerateKakaoNickName.generateKakaoNickName();
        } else {
            return GenerateNaverNickName.generateNaverNickName();
        }
    }

    public User toEntity(SocialType socialType, Oauth2UserInfo oauth2UserInfo) {
        return User.builder()
                .socialType(socialType)
                .socialId(oauth2UserInfo.getId())
                .userId(UUID.randomUUID().toString())
                .nickName(generateNickName(socialType))
                .role(Role.GUEST)
                .build();
    }
}
