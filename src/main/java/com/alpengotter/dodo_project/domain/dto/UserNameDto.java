package com.alpengotter.dodo_project.domain.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserNameDto {
    private String firstName;
    private String lastName;
    private String surname;
}
