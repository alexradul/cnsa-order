package com.polarbookshop.order.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.validation.constraints.NotNull;
import java.net.URI;

@ConfigurationProperties(prefix = "order.polar")
public record ClientProperties(
        @NotNull
        URI catalogServiceUri
) {
}
