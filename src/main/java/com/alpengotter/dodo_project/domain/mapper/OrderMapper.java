package com.alpengotter.dodo_project.domain.mapper;


import com.alpengotter.dodo_project.domain.dto.ExcelDateFilterDto;
import com.alpengotter.dodo_project.domain.dto.OrderResponseDto;
import com.alpengotter.dodo_project.domain.dto.OrderWebhookDto;
import com.alpengotter.dodo_project.domain.dto.OrdersExcelDto;
import com.alpengotter.dodo_project.domain.entity.OrdersEntity;
import com.alpengotter.dodo_project.domain.mapper.service.OrderMapperService;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = OrderMapperService.class
)
public interface OrderMapper {
    @Mapping(target = "employee", source = "orderDto.email", qualifiedByName = "mapEmailToId")
    @Mapping(target = "employeeName", source = "orderDto.email")
    @Mapping(target = "tildaId", source = "orderDto.id")
    @Mapping(target = "total", source = "orderDto.total")
    @Mapping(target = "status", constant = "ACTIVE")
    @Mapping(target = "date", source = "orderDto.date", qualifiedByName = "mapDateFromString")
    @Mapping(target = "id", ignore = true)
    OrdersEntity toOrderEntity(OrderWebhookDto orderDto);

//    @Mapping(target = "email", source = "orderEntity.employee", qualifiedByName = "mapEmployeeToEmail")
    @Mapping(target = "email", constant = "null")
    OrderResponseDto toOrderResponseDto(OrdersEntity orderEntity);

    List<OrderResponseDto> toOrderDtoList(List<OrdersEntity> ordersEntities);

    @Mapping(target = "month", source = "filterDto.month", qualifiedByName = "mapDateToMonth")
    @Mapping(target = "countOrders", source = "filterDto", qualifiedByName = "mapCountOrders")
    OrdersExcelDto toOrderExcelDto(ExcelDateFilterDto filterDto);

    List<OrdersExcelDto> toOrdersExcelDtoList(List<ExcelDateFilterDto> filterDto);

}
