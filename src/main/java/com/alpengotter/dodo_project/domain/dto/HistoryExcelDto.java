package com.alpengotter.dodo_project.domain.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class HistoryExcelDto {
    @ExcelProperty("Месяц")
    @ColumnWidth(30)
    private String month;
    @ExcelProperty("Потрачено зубов")
    @ColumnWidth(18)
    private Integer countLemonsSpend;
    @ExcelProperty("Начислено зубов")
    @ColumnWidth(18)
    private Integer countLemonsAccrued;
//    @ExcelProperty("Потрачено алмазов")
//    @ColumnWidth(18)
//    private Integer countDiamondsSpend;
//    @ExcelProperty("Начислено алмазов")
//    @ColumnWidth(18)
//    private Integer countDiamondsAccrued;
}
