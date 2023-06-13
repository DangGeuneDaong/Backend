package com.dgd.oauth2.generate;

import com.dgd.repository.UserRepository;
import lombok.RequiredArgsConstructor;

import java.util.Random;

@RequiredArgsConstructor
public class GenerateNaverNickName {
    private final UserRepository userRepository;
    public static String generateNaverNickName() {
        int index = 0;
        String[] nick1 = new String[] {
          "멍때리는", "건방진", "잠자는", "신난", "드러누운", "밥먹는"
        };

        String[] nick2 = new String[] {
                "프로도", "라이언", "어피치", "네오", "춘식"
        };

        StringBuilder nickname = new StringBuilder();
        Random random = new Random();
        String nick;
        int cnt = 0;

        for (int i = 0; i < 1 ; i++) {
            double rd = random.nextDouble();
            double idx1 = (nick1.length * rd);
            double idx2 = (nick2.length * rd);
            nickname.append(nick1[(int)idx1]);
            nickname.append(" ");
            nickname.append(nick2[(int)idx2]);
        }
        System.out.println(nickname);
        return nickname.toString();
    }

    public static void main(String[] args) {
        System.out.println(generateNaverNickName());
    }
}
