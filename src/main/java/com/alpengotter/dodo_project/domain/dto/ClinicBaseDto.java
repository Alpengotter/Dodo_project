package com.alpengotter.dodo_project.domain.dto;

import lombok.Data;
import org.springframework.lang.Nullable;

@Data
public class ClinicBaseDto {
    private String name;
    @Nullable
    private Long currency;
}
