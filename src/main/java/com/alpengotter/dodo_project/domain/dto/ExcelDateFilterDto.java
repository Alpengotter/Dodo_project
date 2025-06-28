package com.alpengotter.dodo_project.domain.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ExcelDateFilterDto {
    private Integer month;
    private Integer year;
}
