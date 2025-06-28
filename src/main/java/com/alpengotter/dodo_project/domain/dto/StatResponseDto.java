package com.alpengotter.dodo_project.domain.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StatResponseDto {
    private Integer users;
    private Integer lemons;
    private Integer diamonds;
}
