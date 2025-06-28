package com.alpengotter.dodo_project.domain.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserExcelDto {
    @ExcelProperty("ФИО")
    @ColumnWidth(30)
    private String name;
    @ExcelProperty("Клиника")
    @ColumnWidth(30)
    private String clinic;
    @ExcelProperty("Кол-во зубов")
    @ColumnWidth(30)
    private Integer countLemons;
//    @ExcelProperty("Кол-во алмазов")
//    @ColumnWidth(15)
//    private Integer countDiamonds;

}
