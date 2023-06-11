package com.dgd.controller;

import com.dgd.model.dto.user.SignInDto;
import com.dgd.model.dto.user.SignUpDto;
import com.dgd.model.entity.User;
import com.dgd.repository.UserRepository;
import com.dgd.service.TokenService;
import com.dgd.service.UserSignService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserSignController {
    private final UserSignService userSignService;
    private final TokenService tokenService;

    @PostMapping("/signup")
    public ResponseEntity<User> signUp(@RequestBody @Valid SignUpDto dto) {
        return ResponseEntity.ok(userSignService.signUp(dto));
    }

    @PostMapping("/signin")
    public String signIn(@RequestBody @Valid SignInDto dto) {
        userSignService.signIn(dto);
        return "로그인";
    }
}
