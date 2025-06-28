package com.alpengotter.dodo_project.domain.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class ClinicResponseDto extends ClinicBaseDto {
    private Integer id;
}
