package com.alpengotter.dodo_project.domain.mapper;


import com.alpengotter.dodo_project.domain.dto.ExcelDateFilterDto;
import com.alpengotter.dodo_project.domain.dto.HistoryExcelDto;
import com.alpengotter.dodo_project.domain.dto.HistoryResponseDto;
import com.alpengotter.dodo_project.domain.entity.HistoryEntity;
import com.alpengotter.dodo_project.domain.mapper.service.HistoryMapperService;
import java.util.List;
import java.util.Set;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = HistoryMapperService.class
)
public interface HistoryMapper {

    @Mapping(target = "user", source = "historyEntity.user", qualifiedByName = "mapToUserResponseDto")
    @Mapping(target = "adminId", source = "historyEntity.admin.id")
    @Mapping(target = "orderId", source = "historyEntity.order.id")
    @Mapping(target = "clinic", source = "historyEntity.clinic", qualifiedByName = "mapToClinicNameDto")
    HistoryResponseDto toHistoryResponseDto(HistoryEntity historyEntity);

    List<HistoryResponseDto> toHistoryResponseDtoList(List<HistoryEntity> historyEntity);
    List<HistoryResponseDto> toHistoryResponseDtoList(Set<HistoryEntity> historyEntity);

    @Mapping(target = "month", source = "filterDto.month", qualifiedByName = "mapDateToMonth")
    @Mapping(target = "countLemonsSpend", source = "filterDto", qualifiedByName = "mapCountLemonsSpend")
    @Mapping(target = "countLemonsAccrued", source = "filterDto", qualifiedByName = "mapCountLemonsAccrued")
//    @Mapping(target = "countDiamondsSpend", source = "filterDto", qualifiedByName = "mapCountDiamondsSpend")
//    @Mapping(target = "countDiamondsAccrued", source = "filterDto", qualifiedByName = "mapCountDiamondsAccrued")
    HistoryExcelDto toHistoryExcelDto(ExcelDateFilterDto filterDto);

    List<HistoryExcelDto> toHistoryExcelDtoList(List<ExcelDateFilterDto> excelDateFilterDtoList);

}
