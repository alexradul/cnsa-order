package com.polarbookshop.order.config;

import com.polarbookshop.order.book.BookCatalog;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class ClientConfig {
    @Bean
    WebClient webClient(BookCatalogClientProperties properties, WebClient.Builder builder) {
        return builder
                .baseUrl(properties.catalogServiceUri().toString())
                .build();
    }

    @Bean
    BookCatalog bookClient(WebClient webClient, BookCatalogClientProperties clientProperties) {
        return new BookCatalog(webClient, clientProperties);
    }
}
