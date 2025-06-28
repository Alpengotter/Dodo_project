package com.alpengotter.dodo_project.service;

import com.alpengotter.dodo_project.domain.dto.AnalitiqueResponseDto;
import com.alpengotter.dodo_project.domain.dto.AnalitiqueSummaryResponseDto;
import com.alpengotter.dodo_project.domain.entity.AnalitiqueEntity;
import com.alpengotter.dodo_project.domain.entity.HistoryEntity;
import com.alpengotter.dodo_project.domain.mapper.AnalitiqueMapper;
import com.alpengotter.dodo_project.domain.repository.AnalitiqueRepository;
import com.alpengotter.dodo_project.domain.repository.HistoryRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class AnalitiqueService {
    private final AnalitiqueRepository analitiqueRepository;
    private final AnalitiqueMapper analitiqueMapper;
    private final HistoryRepository historyRepository;
    private static final String LEMONS_ACCRUED_BY_USER = "lemons_accrued_by_user";
    private static final String LEMONS_ACCRUED_BY_CLINIC = "lemons_accrued_by_clinic";

    @Transactional
    public List<AnalitiqueResponseDto> getAnalitique(String type, Integer year, Integer month, Integer day) {
        List<AnalitiqueEntity> analitiqueEntityList = analitiqueRepository
            .findByTypeAndDate(type, year, month, day);
        return analitiqueMapper.toAnalitiqueResponseDtoList(analitiqueEntityList);
    }

    @Transactional
    public void saveAnalitique(String type,
                                Integer count,
                                String currency) {
        AnalitiqueEntity analitiqueEntity = AnalitiqueEntity.builder()
            .type(type)
            .count(count)
            .currency(currency)
            .date(LocalDateTime.now())
            .build();
        analitiqueRepository.save(analitiqueEntity);
    }

    @Transactional
    public List<AnalitiqueSummaryResponseDto> getAnalitiqueSummary(List<String> types, Integer year) {
        List<AnalitiqueSummaryResponseDto> result = new ArrayList<>();
        if (Objects.isNull(types)) {
            types = new ArrayList<>();
            types.addAll(List.of(
                "orders_processed",
                LEMONS_ACCRUED_BY_USER,
                LEMONS_ACCRUED_BY_CLINIC,
                "lemons_spend",
                "diamonds_accrued",
                "diamonds_spend",
                "new_employer"));
        }

        Map<String, List<HistoryEntity>> analitiqueForTypes = getAnalitiqueForTypes(types, year);

        for (String type : types) {
            //TODO Посмотреть, как это можно оптимизировать
            List<Integer> totalMonthList = new ArrayList<>(Collections.nCopies(12, 0));
            Integer total = 0;
            List<HistoryEntity> analitiqueEntities = analitiqueForTypes.get(type);
            for (HistoryEntity entity: analitiqueEntities) {
                if (StringUtils.contains(entity.getType(), "reward") ||
                    StringUtils.equals(entity.getType(), "order")) {
                    int monthValue = entity.getDate().getMonthValue();
                    totalMonthList.set(monthValue - 1, totalMonthList.get(monthValue - 1) + Math.abs(entity.getValue()));
                    total += Math.abs(entity.getValue());
                } else {
                    int monthValue = entity.getDate().getMonthValue();
                    totalMonthList.set(monthValue - 1, totalMonthList.get(monthValue - 1) + 1);
                    total += 1;
                }
            }
            result.add(AnalitiqueSummaryResponseDto.builder()
                    .type(StringUtils.toRootUpperCase(type))
                    .totalMounth(totalMonthList)
                    .total(total)
                .build());
        }

        return result;
    }

    private Map<String, List<HistoryEntity>> getAnalitiqueForTypes(List<String> types, Integer year) {
        Map<String, List<HistoryEntity>> result = new HashMap<>();
        for (String type : types) {
            switch (type) {
//                case "orders_processed" -> {
//                    List<AnalitiqueEntity> countProcessedOrders = analitiqueRepository.findAllProcessedOrders(
//                        null, year);
//                    result.put("orders_processed", countProcessedOrders);
//                }
                case LEMONS_ACCRUED_BY_USER -> {
                    List<HistoryEntity> findByLemonsAccrued = historyRepository.findByLemonsAccruedByUser(null, year);
                    result.put(LEMONS_ACCRUED_BY_USER, findByLemonsAccrued);
                }
                case LEMONS_ACCRUED_BY_CLINIC -> {
                    List<HistoryEntity> findByLemonsAccrued = historyRepository.findByLemonsAccruedByClinic(null, year);
                    result.put(LEMONS_ACCRUED_BY_CLINIC, findByLemonsAccrued);
                }
//                case "lemons_spend" -> {
//                    List<AnalitiqueEntity> findByLemonsSpend = analitiqueMapper.toAnalitiqueEntityList(
//                        historyRepository.findByLemonsSpend(null, year));
//                    result.put("lemons_spend", findByLemonsSpend);
//                }
//                case "diamonds_spend" -> {
//                    List<AnalitiqueEntity> findByDiamondsSpend = analitiqueMapper.toAnalitiqueEntityList(
//                        historyRepository.findByDiamondsSpend(null, year));
//                    result.put("diamonds_spend", findByDiamondsSpend);
//                }
//                case "diamonds_accrued" -> {
//                    List<AnalitiqueEntity> findByDiamondsAccrued = analitiqueMapper.toAnalitiqueEntityList(
//                        historyRepository.findByDiamondsAccrued(null, year));
//                    result.put("diamonds_accrued", findByDiamondsAccrued);
//                }
//                case "new_employer" -> {
//                    List<AnalitiqueEntity> findByNewEmployers = analitiqueRepository.findNewEmployers(year);
//                    result.put("new_employer", findByNewEmployers);
//                }
                default -> throw new IllegalArgumentException("Unknown type: " + type);
            }
        }
        return result;
    }

    public List<AnalitiqueSummaryResponseDto> getAnalitiqueSummaryByComment(Integer year) {
        List<AnalitiqueSummaryResponseDto> result = new ArrayList<>();
        List<String> uniqueComments = historyRepository.findUniqueCommentsByYear(year);
        Map<String, List<HistoryEntity>> analitiqueForComments = getAnalitiqueForComments(uniqueComments, year);

        for (String comment : uniqueComments) {
            List<Integer> totalMonthList = new ArrayList<>(Collections.nCopies(12, 0));
            Integer total = 0;
            List<HistoryEntity> analitiqueEntities = analitiqueForComments.get(comment);
            for (HistoryEntity entity: analitiqueEntities) {
                int monthValue = entity.getDate().getMonthValue();
                totalMonthList.set(monthValue - 1, totalMonthList.get(monthValue - 1) + Math.abs(entity.getValue()));
                total += Math.abs(entity.getValue());
            }
            result.add(AnalitiqueSummaryResponseDto.builder()
                .type(comment)
                .totalMounth(totalMonthList)
                .total(total)
                .build());
        }
        return result;
    }


    private Map<String, List<HistoryEntity>> getAnalitiqueForComments(List<String> comments, Integer year) {
        Map<String, List<HistoryEntity>> result = new HashMap<>();
        for (String comment : comments) {
            result.put(comment, historyRepository.findByLemonsAccruedByComment(null, year, comment));
        }
        return result;
    }
}
