package com.alpengotter.dodo_project.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class JwtRequestDto {
    private String email;
    private String password;
}
