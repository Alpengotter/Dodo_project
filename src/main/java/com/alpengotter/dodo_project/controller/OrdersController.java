package com.alpengotter.dodo_project.controller;

import com.alpengotter.dodo_project.domain.dto.OrderResponseDto;
import com.alpengotter.dodo_project.domain.dto.OrderUpdateStatusDto;
import com.alpengotter.dodo_project.domain.dto.OrderWebhookDto;
import com.alpengotter.dodo_project.service.OrdersService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrdersController {
    private final OrdersService ordersService;

    @PostMapping("/create")
    public OrderResponseDto createNewOrder(@RequestBody OrderWebhookDto orderDto) {
        return ordersService.createNewOrder(orderDto);
    }

    @GetMapping("/{id}")
    public OrderResponseDto getOrderById(@PathVariable("id") Integer id) {
        return ordersService.getOrderById(id);
    }

    @GetMapping
    public List<OrderResponseDto> getAllActiveOrders() {
        return ordersService.getAllActiveOrders();
    }

    @PutMapping("/change-status")
    public OrderResponseDto changeStatusById(@RequestBody OrderUpdateStatusDto orderDto) {
        return ordersService.changeStatusById(orderDto);
    }

}
