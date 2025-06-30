package com.alpengotter.dodo_project.domain.dto;

import java.util.List;
import lombok.Data;
import org.springframework.lang.Nullable;

@Data
public class UserLowResponseDto {
    private Integer id;
    private String firstName;
    private String lastName;
    private String surname;
    private Integer lemons;
    private Integer diamonds;
    @Nullable
    private String jobTitle;
}
