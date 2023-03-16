package com.polarbookshop.order.web;

import javax.validation.constraints.*;

public record OrderRequest(
        @NotBlank(message = "The book ISBN must be defined.")
        String isbn,
        @NotNull(message = "The book quanity must be defined")
        @Min(value = 1, message = "You must order at least one item")
        @Max(value = 5, message = "You cannot order more then five items.")
        Integer quantity) {
}
