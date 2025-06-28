package com.alpengotter.dodo_project.domain.mapper.service;

import com.alpengotter.dodo_project.domain.dto.ExcelDateFilterDto;
import com.alpengotter.dodo_project.domain.entity.UserEntity;
import com.alpengotter.dodo_project.domain.repository.AnalitiqueRepository;
import com.alpengotter.dodo_project.domain.repository.OrdersRepository;
import com.alpengotter.dodo_project.domain.repository.UserRepository;
import com.alpengotter.dodo_project.handler.ErrorType;
import com.alpengotter.dodo_project.handler.exception.LemonBankException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.mapstruct.Named;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderMapperService {
    private final UserRepository userRepository;
    private final OrdersRepository ordersRepository;
    private final AnalitiqueRepository analitiqueRepository;

    @Named("mapEmailToId")
    public UserEntity mapEmailToId(String email) {
        Optional<UserEntity> user = userRepository.findByEmailContainingIgnoreCaseAndIsActiveIsTrue(email);
        if (user.isPresent()) {
            return user.get();
        } else {
            throw new LemonBankException(ErrorType.USER_NOT_FOUND);
        }
    }

    @Named("mapDateFromString")
    public LocalDateTime mapDateFromString(String dateString) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        return LocalDateTime.of(LocalDate.parse(dateString, formatter), LocalTime.now());
    }

    @Named("mapDateToMonth")
    public String mapDateToMonth(Integer monthNumber) {
        Locale russianLocale = new Locale("ru", "RU");
        return Month.of(monthNumber).getDisplayName(
            java.time.format.TextStyle.FULL_STANDALONE, russianLocale
        );
    }

    @Named("mapCountOrders")
    public Integer countOrders(ExcelDateFilterDto filterDto) {
        return analitiqueRepository.countAllProcessedOrders(filterDto.getMonth(), filterDto.getYear());
    }
}
