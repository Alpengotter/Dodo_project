package com.alpengotter.dodo_project.controller;

import com.alpengotter.dodo_project.domain.dto.AnalitiqueResponseDto;
import com.alpengotter.dodo_project.domain.dto.AnalitiqueSummaryResponseDto;
import com.alpengotter.dodo_project.service.AnalitiqueService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/analitique")
@RequiredArgsConstructor
public class AnalitiqueController {

    private final AnalitiqueService analitiqueService;

    @Deprecated
    @GetMapping("/get-analitique")
    public List<AnalitiqueResponseDto> getAnalitique(
        @RequestParam("type") String type,
        @RequestParam(value = "year", required = false) Integer year,
        @RequestParam(value = "month", required = false) Integer month,
        @RequestParam(value = "day", required = false) Integer day) {
        return analitiqueService.getAnalitique(type, year, month, day);
    }

    @GetMapping("/get-analitique-summary")
    public List<AnalitiqueSummaryResponseDto> getAnalitiqueSummary(
        @RequestParam(value = "type", required = false) List<String> typeList,
        @RequestParam(value = "year") Integer year) {
        return analitiqueService.getAnalitiqueSummary(typeList, year);
    }

    @GetMapping("/get-analitique-summary-comment")
    public List<AnalitiqueSummaryResponseDto> getAnalitiqueSummaryByComment(
        @RequestParam(value = "year") Integer year) {
        return analitiqueService.getAnalitiqueSummaryByComment(year);
    }

}
