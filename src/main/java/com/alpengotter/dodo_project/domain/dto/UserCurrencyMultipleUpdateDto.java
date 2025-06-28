package com.alpengotter.dodo_project.domain.dto;

import java.util.List;
import lombok.Data;

@Data
public class UserCurrencyMultipleUpdateDto {
    private List<Integer> userIds;
    private Integer count;
    private String currency;
    private String comment;
}
