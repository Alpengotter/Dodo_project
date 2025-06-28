package com.alpengotter.dodo_project.domain.dto;

import lombok.Data;

@Data
public class AdminRegisterDto {
    private String email;
    private String password;
    private String role;

}
