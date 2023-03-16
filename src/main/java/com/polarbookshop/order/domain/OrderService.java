package com.polarbookshop.order.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository repository;

    public Flux<Order> getAllOrders() {
        return repository.findAll();
    }

    public Mono<Order> submit(String isbn, int quantity) {
        return Mono.just(buildRejectedOrder(isbn, quantity))
                .flatMap(repository::save);
    }

    private Order buildRejectedOrder(String isbn, int quantity) {
        return Order.of(isbn, null, 0, quantity, OrderStatus.REJECTED);
    }

}
