package com.polarbookshop.order.domain;

import com.polarbookshop.order.book.Book;
import com.polarbookshop.order.book.BookCatalog;
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

    private static Order buildRejectedOrder(String isbn, int quantity) {
        return Order.of(isbn, null, 0, quantity, OrderStatus.REJECTED);
    }

}
