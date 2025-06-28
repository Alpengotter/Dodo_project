package com.alpengotter.dodo_project.service;

import com.alpengotter.dodo_project.domain.dto.HistoryResponseDto;
import com.alpengotter.dodo_project.domain.entity.ClinicEntity;
import com.alpengotter.dodo_project.domain.entity.HistoryEntity;
import com.alpengotter.dodo_project.domain.entity.OrdersEntity;
import com.alpengotter.dodo_project.domain.entity.UserEntity;
import com.alpengotter.dodo_project.domain.mapper.HistoryMapper;
import com.alpengotter.dodo_project.domain.repository.HistoryRepository;
import com.alpengotter.dodo_project.domain.repository.UserRepository;
import com.alpengotter.dodo_project.handler.ErrorType;
import com.alpengotter.dodo_project.handler.exception.LemonBankException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class HistoryService {
    private final HistoryRepository historyRepository;
    private final UserRepository userRepository;
    private final HistoryMapper historyMapper;

    @Transactional
    public void addOrderInHistory(OrdersEntity orders) {
        Integer adminId = Integer.parseInt((String) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        log.debug("Admin id:{}", adminId);
        Optional<UserEntity> admin = userRepository.findByIdAndIsActiveIsTrue(
            adminId);
        if (admin.isEmpty()) {
            throw new LemonBankException(ErrorType.ADMIN_NOT_FOUND);
        }

//        String dateString = orders.getDate();
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
//        LocalDate date = LocalDate.parse(dateString, formatter);
//        log.debug("Parse date:{}",date);
        HistoryEntity historyEntity = HistoryEntity.builder()
            .admin(admin.get())
            .user(orders.getEmployee())
            .date(LocalDateTime.now())
            .type("order")
            .comment("Подтверждение заказа")
            .order(orders)
            .currency("lemons")
            .value(-orders.getTotal())
            .build();
        historyRepository.save(historyEntity);
    }

    @Transactional
    public void changeCurrency(UserEntity user, Integer differenceLemons, Integer differenceDiamonds, String comment) {
        Integer adminId = Integer.parseInt((String) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        log.debug("Admin id:{}", adminId);
        Optional<UserEntity> admin = userRepository.findByIdAndIsActiveIsTrue(
            adminId);
        if (admin.isEmpty()) {
            throw new LemonBankException(ErrorType.ADMIN_NOT_FOUND);
        }
        if (differenceLemons != 0) {
            HistoryEntity historyLemons = HistoryEntity.builder()
                .admin(admin.get())
                .user(user)
                .date(LocalDateTime.now())
                .type("reward")
                .comment(comment)
                .order(null)
                .currency("lemons")
                .value(differenceLemons)
                .build();
            historyRepository.save(historyLemons);
        }

        if (differenceDiamonds != 0) {
            HistoryEntity historyDiamonds = HistoryEntity.builder()
                .admin(admin.get())
                .user(user)
                .date(LocalDateTime.now())
                .type("reward")
                .comment(comment)
                .order(null)
                .currency("diamonds")
                .value(differenceDiamonds)
                .build();
            historyRepository.save(historyDiamonds);
        }

    }

    @Transactional
    public void changeCurrencyClinic(ClinicEntity clinic, Integer differenceLemons, String comment) {
        Integer adminId = Integer.parseInt((String) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        log.debug("Admin id:{}", adminId);
        Optional<UserEntity> admin = userRepository.findByIdAndIsActiveIsTrue(
            adminId);
        if (admin.isEmpty()) {
            throw new LemonBankException(ErrorType.ADMIN_NOT_FOUND);
        }
        if (differenceLemons != 0) {
            HistoryEntity historyLemons = HistoryEntity.builder()
                .admin(admin.get())
                .date(LocalDateTime.now())
                .type("reward")
                .comment(comment)
                .order(null)
                .currency("lemons")
                .value(differenceLemons)
                .clinic(clinic)
                .build();
            historyRepository.save(historyLemons);
        }
    }

    @Transactional
    public List<HistoryResponseDto> getHistoryByDateAndParam(String dateFromString, String dateToString,
        String searchParameter, List<Integer> clinicIds) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        LocalDate dateFrom = LocalDate.parse(dateFromString, formatter);
        LocalDate dateTo = LocalDate.parse(dateToString, formatter);
        LocalDateTime dateTimeFrom = dateFrom.atStartOfDay();
        LocalDateTime dateTimeTo = dateTo.atStartOfDay();
//        if (isEnglishSymbols(searchParameter)) {
//            List<HistoryEntity> historyEntities = historyRepository.findAllByDateBetweenAndUserEmailContainingOrderByIdDesc(
//                dateTimeFrom, dateTimeTo, searchParameter);
//            return historyMapper.toHistoryResponseDtoList(historyEntities);
//        } else {
            Set<HistoryEntity> historyEntities =
                historyRepository.findAllByDateBetweenAndUserFirstNameContainingOrUserLastNameContainingOrderByIdDesc(
                    dateTimeFrom, dateTimeTo, searchParameter, searchParameter, searchParameter, searchParameter, clinicIds);
//            historyEntities.addAll(historyRepository.findByComment(searchParameter, dateTimeFrom, dateTimeTo));
            return historyMapper.toHistoryResponseDtoList(historyEntities);
//        }
    }

    private boolean isEnglishSymbols(String value) {
        return value.matches("^[a-zA-Z0-9.@]+$");
    }

    @Transactional
    public List<HistoryResponseDto> getHistoryClinicById(Integer id) {
        List<HistoryEntity> historyEntities = historyRepository.findAllByClinicIdOrderByIdDesc(id);
        return historyMapper.toHistoryResponseDtoList(historyEntities);
    }

    @Transactional
    public List<HistoryResponseDto> getHistoryUsersById(Integer id) {
        List<HistoryEntity> historyEntities = historyRepository.findAllByUserIdOrderByIdDesc(id);
        return historyMapper.toHistoryResponseDtoList(historyEntities);
    }

    @Transactional
    public void deleteHistoryById(Integer id) {
        Optional<HistoryEntity> history = historyRepository.findById(id);
        if (history.isEmpty()) {
            log.info("History not found");
            throw new LemonBankException(ErrorType.HISTORY_NOT_FOUND);
        }
        HistoryEntity historyEntity = history.get();
        int diffCurrency = historyEntity.getValue();
        if (historyEntity.getClinic() != null) {
            long currentCurrency;
            ClinicEntity clinic = historyEntity.getClinic();
            log.info("Clinic:{}", clinic);
            currentCurrency = clinic.getCurrency();
            clinic.setCurrency(currentCurrency + (-1 * diffCurrency));
//            historyRepository.delete(historyEntity);
        } else if (historyEntity.getUser() != null) {
            int currentCurrency;
            UserEntity user = historyEntity.getUser();
            log.info("User:{}", user);
            currentCurrency = user.getLemons();
            user.setLemons(currentCurrency + (-1 * diffCurrency));
//            historyRepository.delete(historyEntity);
        } else {
            log.info("User and clinic is null");
            throw new LemonBankException(ErrorType.SERVER_ERROR);
        }
        historyRepository.delete(historyEntity);
    }
}
