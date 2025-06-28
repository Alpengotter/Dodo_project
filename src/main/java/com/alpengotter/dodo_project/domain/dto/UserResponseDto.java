package com.alpengotter.dodo_project.domain.dto;

import java.util.List;
import lombok.Data;
import org.springframework.lang.Nullable;

@Data
public class UserResponseDto {
    private Integer id;
    private String firstName;
    private String lastName;
    private String surname;
    private String email;
    private Integer lemons;
    private Integer diamonds;
    private String userRole;
    @Nullable
    private String jobTitle;
    @Nullable
    private Boolean isActive;
    @Nullable
    private List<String> clinics;
}
