package com.alpengotter.dodo_project.domain.mapper.service;

import com.alpengotter.dodo_project.domain.dto.ClinicNameDto;
import com.alpengotter.dodo_project.domain.dto.ExcelDateFilterDto;
import com.alpengotter.dodo_project.domain.dto.UserNameDto;
import com.alpengotter.dodo_project.domain.dto.UserResponseDto;
import com.alpengotter.dodo_project.domain.entity.ClinicEntity;
import com.alpengotter.dodo_project.domain.entity.UserEntity;
import com.alpengotter.dodo_project.domain.mapper.UserMapper;
import com.alpengotter.dodo_project.domain.repository.HistoryRepository;
import java.time.Month;
import java.util.Locale;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.mapstruct.Named;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HistoryMapperService {
    private final UserMapper userMapper;
    private final HistoryRepository historyRepository;

    @Named("mapDateToMonth")
    public String mapDateToMonth(Integer monthNumber) {
        Locale russianLocale = new Locale("ru", "RU");
        return Month.of(monthNumber).getDisplayName(
            java.time.format.TextStyle.FULL_STANDALONE, russianLocale
        );
    }

    @Named("mapToUserResponseDto")
    public UserResponseDto mapToUserResponseDto(UserEntity entity) {
        return userMapper.toUserResponseDto(entity);
    }

    @Named("mapToUserNameDto")
    public UserNameDto mapDateToMonth(UserEntity user) {
        return Optional.ofNullable(user)
            .map(u -> UserNameDto.builder()
            .firstName(u.getFirstName())
            .lastName(u.getLastName())
            .surname(u.getSurname())
            .build())
            .orElse(null);
    }

    @Named("mapToClinicNameDto")
    public ClinicNameDto mapDateToMonth(ClinicEntity clinic) {
        return Optional.ofNullable(clinic)
            .map(c -> ClinicNameDto.builder()
            .name(c.getName())
            .build())
            .orElse(null);
    }

    @Named("mapCountLemonsSpend")
    public Integer mapCountLemontSpend(ExcelDateFilterDto filterDto) {
        return Optional.ofNullable(historyRepository.countLemonsSpend(filterDto.getMonth(), filterDto.getYear()))
            .orElse(0);
    }

    @Named("mapCountLemonsAccrued")
    public Integer mapCountLemontAccrued(ExcelDateFilterDto filterDto) {
        return Optional.ofNullable(historyRepository.countLemonsAccrued(filterDto.getMonth(), filterDto.getYear()))
            .orElse(0);
    }

    @Named("mapCountDiamondsSpend")
    public Integer mapCountDiamondsSpend(ExcelDateFilterDto filterDto) {
        return Optional.ofNullable(historyRepository.countDiamondsSpend(filterDto.getMonth(), filterDto.getYear()))
            .orElse(0);
    }

    @Named("mapCountDiamondsAccrued")
    public Integer mapCountDiamondsAccrued(ExcelDateFilterDto filterDto) {
        return Optional.ofNullable(historyRepository.countDiamondsAccrued(filterDto.getMonth(), filterDto.getYear()))
            .orElse(0);
    }
}
