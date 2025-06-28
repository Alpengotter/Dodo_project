package com.alpengotter.dodo_project.controller;

import com.alpengotter.dodo_project.domain.dto.HistoryResponseDto;
import com.alpengotter.dodo_project.service.HistoryService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/history")
//@CrossOrigin(origins = {"https://bankoflemons.ru", "https://store.zarplata.ru", "https://uat.bankoflemons.ru"})
@RequiredArgsConstructor
public class HistoryController {
    private final HistoryService historyService;

    @GetMapping("/find-by-date-and-param")
    public List<HistoryResponseDto> getHistoryByDateAndParam(@RequestParam("dateFrom") String dateFrom,
                                         @RequestParam("dateTo") String dateTo,
                                         @RequestParam("searchParameter") String searchParameter,
        @RequestParam(value = "clinicIds", required = false) List<Integer> clinicIds) {
        return historyService.getHistoryByDateAndParam(dateFrom, dateTo, searchParameter, clinicIds);
    }

    @GetMapping("/find-by-id")
    public List<HistoryResponseDto> getHistoryUsersById(@RequestParam("id") Integer id) {
        return historyService.getHistoryUsersById(id);
    }

    @GetMapping("/find-by-clinics-id")
    public List<HistoryResponseDto> getHistoryClinicsById(@RequestParam("id") Integer id) {
        return historyService.getHistoryClinicById(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHistoryById(@PathVariable("id") Integer id) {
        historyService.deleteHistoryById(id);
        return ResponseEntity.noContent().build();
    }

}
