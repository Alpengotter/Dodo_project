package com.alpengotter.dodo_project.service;

import com.alpengotter.dodo_project.domain.dto.OrderResponseDto;
import com.alpengotter.dodo_project.domain.dto.OrderUpdateStatusDto;
import com.alpengotter.dodo_project.domain.dto.OrderWebhookDto;
import com.alpengotter.dodo_project.domain.entity.OrdersEntity;
import com.alpengotter.dodo_project.domain.entity.UserEntity;
import com.alpengotter.dodo_project.domain.enums.AnalitiqueType;
import com.alpengotter.dodo_project.domain.mapper.OrderMapper;
import com.alpengotter.dodo_project.domain.repository.OrdersRepository;
import com.alpengotter.dodo_project.domain.repository.UserRepository;
import com.alpengotter.dodo_project.handler.ErrorType;
import com.alpengotter.dodo_project.handler.exception.LemonBankException;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrdersService {
    private final OrderMapper orderMapper;
    private final OrdersRepository ordersRepository;
    private final HistoryService historyService;
    private final AnalitiqueService analitiqueService;
    private final UserRepository userRepository;

    @Transactional
    public OrderResponseDto createNewOrder(OrderWebhookDto orderDto) {
        OrdersEntity orderEntity = orderMapper.toOrderEntity(orderDto);
        OrdersEntity savedOrder = ordersRepository.save(orderEntity);
        analitiqueService.saveAnalitique(
            AnalitiqueType.NEW_ORDER.getMessage(),
            savedOrder.getTotal(),
            "lemons");
        return orderMapper.toOrderResponseDto(savedOrder);
    }

    @Transactional
    public OrderResponseDto getOrderById(Integer id) {
        Optional<OrdersEntity> order = ordersRepository.findById(id);
        if (order.isPresent()) {
            return orderMapper.toOrderResponseDto(order.get());
        } else  {
            throw new LemonBankException(ErrorType.ORDER_NOT_FOUND);
        }
    }

    @Transactional
    public List<OrderResponseDto> getAllActiveOrders() {
        List<OrdersEntity> allActiveOrders = ordersRepository.findAllActiveOrders();
        return orderMapper.toOrderDtoList(allActiveOrders);
    }

    @Transactional
    public OrderResponseDto changeStatusById(OrderUpdateStatusDto orderDto) {
        Optional<OrdersEntity> order = ordersRepository.findById(orderDto.getId());
        if (order.isEmpty()) {
            throw new LemonBankException(ErrorType.ORDER_NOT_FOUND);
        }
        OrdersEntity ordersEntity = order.get();
        String status = orderDto.getStatus();
        ordersEntity.setStatus(status);
        OrdersEntity saved = ordersRepository.save(ordersEntity);

        if (status.equals("ACCEPTED")) {
            analitiqueService.saveAnalitique(AnalitiqueType.ACCEPT_ORDER.getMessage(),
                -ordersEntity.getTotal(),
                "lemons");
            UserEntity employee = ordersEntity.getEmployee();
            Integer employeeLemons = employee.getLemons();
            employee.setLemons(employeeLemons - ordersEntity.getTotal());
            userRepository.saveAndFlush(employee);
            historyService.addOrderInHistory(saved);
        } else {
            analitiqueService.saveAnalitique(AnalitiqueType.DECLINE_ORDER.getMessage(),
                ordersEntity.getTotal(),
                "lemons");
        }

        return orderMapper.toOrderResponseDto(saved);
    }
}
