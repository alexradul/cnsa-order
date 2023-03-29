package com.polarbookshop.order.domain;

import com.polarbookshop.order.book.Book;
import com.polarbookshop.order.book.BookCatalog;
import com.polarbookshop.order.event.OrderDispatchedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository repository;
    private final BookCatalog catalog;

    public Flux<Order> getAllOrders() {
        return repository.findAll();
    }

    public Mono<Order> submit(String isbn, int quantity) {
        return catalog.getBookByIsbn(isbn)
                .map(book -> buildAcceptedOrder(book, quantity))
                .defaultIfEmpty(buildRejectedOrder(isbn, quantity))
                .flatMap(repository::save);
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
                existingOrder.version()
        );
    }
}
