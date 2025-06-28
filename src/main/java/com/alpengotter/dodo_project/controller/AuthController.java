package com.alpengotter.dodo_project.controller;

import com.alpengotter.dodo_project.domain.dto.AdminRegisterDto;
import com.alpengotter.dodo_project.domain.dto.JwtRequestDto;
import com.alpengotter.dodo_project.domain.dto.JwtResponseDto;
import com.alpengotter.dodo_project.domain.entity.UserEntity;
import com.alpengotter.dodo_project.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
//@CrossOrigin(origins = {"https://bankoflemons.ru", "https://store.zarplata.ru", "https://uat.bankoflemons.ru"})
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public JwtResponseDto getAccessToken(@RequestBody JwtRequestDto requestDto) {
        return authService.getNewUserTokenFromLogin(requestDto);
    }

    @PostMapping("/register")
    public UserEntity registerNewUser(@RequestBody AdminRegisterDto adminRegisterDto) {
        return authService.registerNewUser(adminRegisterDto);
    }


}
