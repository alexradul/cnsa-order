package com.polarbookshop.order.web;

import com.polarbookshop.order.domain.Order;
import com.polarbookshop.order.domain.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

@RestController
@RequestMapping("orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService service;

    @GetMapping
    public Flux<Order> getAllOrders() {
        return service.getAllOrders();
    }

    @PostMapping
    public Mono<Order> submitOrder(@RequestBody @Valid OrderRequest request) {
        return service.submit(request.isbn() , request.quantity());
    }
}
