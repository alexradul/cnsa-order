package com.polarbookshop.order.domain;

import com.polarbookshop.order.book.Book;
import com.polarbookshop.order.book.BookCatalog;
import com.polarbookshop.order.event.OrderAcceptedEvent;
import com.polarbookshop.order.event.OrderDispatchedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository repository;
    private final BookCatalog catalog;
    private final StreamBridge streamBridge;

    public Flux<Order> getAllOrders() {
        return repository.findAll();
    }

    public Mono<Order> submit(String isbn, int quantity) {
        return catalog.getBookByIsbn(isbn)
                .map(book -> buildAcceptedOrder(book, quantity))
                .defaultIfEmpty(buildRejectedOrder(isbn, quantity))
                .flatMap(repository::save)
                .doOnNext(this::publishOrderAcceptedEvent);
    }

    private static Order buildAcceptedOrder(Book book, int quantity) {
        return Order.of(book, quantity, OrderStatus.ACCEPTED);
    }

    public static Order buildRejectedOrder(String isbn, int quantity) {
        return Order.of(isbn, null, 0, quantity, OrderStatus.REJECTED);
    }

    public Flux<Order> consumeOrderDispatchedEvent(Flux<OrderDispatchedEvent> eventStream) {
        return eventStream
                .flatMap(event -> repository.findById(event.orderId()))
                .map(this::buildDispatchedOrder)
                .flatMap(repository::save);
    }

    private Order buildDispatchedOrder(Order existingOrder) {
        return new Order(
                existingOrder.id(),
                existingOrder.bookIsbn(),
                existingOrder.bookName(),
                existingOrder.bookPrice(),
                existingOrder.quantity(),
                OrderStatus.DISPATCHED,
                existingOrder.createdDate(),
                existingOrder.lastModifiedDate(),
                existingOrder.version(),
                existingOrder.createdBy(),
                existingOrder.lastModifiedBy()
        );
    }

    private void publishOrderAcceptedEvent(Order order) {
        if (!order.status().equals(OrderStatus.ACCEPTED)) {
            return;
        }

        var event = new OrderAcceptedEvent(order.id());
        log.info("Sending order accepted event for order with id {}", order.id());
        var result = streamBridge.send("acceptOrder-out-0", event);
        log.info("Result of sending {}: {}", event, result);
    }
}
