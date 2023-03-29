package com.polarbookshop.order.event;

import com.polarbookshop.order.domain.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Flux;

import java.util.function.Consumer;

@Slf4j
@Configuration
public class OrderFunctions {
    @Bean
    Consumer<Flux<OrderDispatchedEvent>> orderDispatched(OrderService orderService) {
        return flux -> orderService.consumeOrderDispatchedEvent(flux)
                .doOnNext(order -> log.info("The order with id {} is dispatched", order.id()))
                .subscribe();
    }

}
