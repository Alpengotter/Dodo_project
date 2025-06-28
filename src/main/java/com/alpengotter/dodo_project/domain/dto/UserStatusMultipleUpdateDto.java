package com.alpengotter.dodo_project.domain.dto;

import java.util.List;
import lombok.Data;

@Data
public class UserStatusMultipleUpdateDto {
    private List<Integer> userIds;
    private Boolean isActive;
}
