package com.polarbookshop.order.book;

import com.polarbookshop.order.config.BookCatalogClientProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;

@RequiredArgsConstructor
public class BookCatalog {
    private static final String BOOKS_ROOT_API = "/books/";
    private final WebClient webClient;
    private final BookCatalogClientProperties clientProperties;

    public Mono<Book> getBookByIsbn(String isbn) {
        return webClient
                .get()
                .uri(BOOKS_ROOT_API + isbn)
                .retrieve()
                .bodyToMono(Book.class)
                .timeout(Duration.ofSeconds(clientProperties.timeout()), Mono.empty())
                .onErrorResume(WebClientResponseException.NotFound.class, exception -> Mono.empty())
                .retryWhen(
                        Retry
                                .backoff(
                                        clientProperties.maxAttempts(),
                                        Duration.ofMillis(clientProperties.retryBackoff())))
                .onErrorResume(Exception.class, ex -> Mono.empty());
    }
}
