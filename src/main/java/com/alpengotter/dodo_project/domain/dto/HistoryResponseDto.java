package com.alpengotter.dodo_project.domain.dto;

import java.time.LocalDate;
import lombok.Data;

@Data
public class HistoryResponseDto {
    private Integer id;
    private UserResponseDto user;
    private Integer adminId;
    private ClinicNameDto clinic;
    private LocalDate date;
    private String type;
    private String comment;
    private Integer orderId;
    private String currency;
    private Integer value;
}
