package com.polarbookshop.order.domain;

import com.polarbookshop.order.book.Book;
import org.springframework.data.annotation.*;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Table("orders")
public record Order(
        @Id
        Long id,
        String bookIsbn,
        String bookName,

        double bookPrice,
        int quantity,
        OrderStatus status,
        @CreatedDate
        LocalDateTime createdDate,
        @LastModifiedDate
        LocalDateTime lastModifiedDate,
        @Version
        int version,

        @CreatedBy
        String createdBy,

        @LastModifiedBy
        String lastModifiedBy
        ) {
    public static Order of(String bookIsbn, String bookName, double bookPrice, int quantity, OrderStatus status) {
        return new Order(null, bookIsbn, bookName, bookPrice, quantity, status, null, null, 0, null, null);
    }

    public static Order of(Book book, int quantity, OrderStatus status) {
        return of(book.isbn(), book.title(), book.price(), quantity, status);
    }
}
