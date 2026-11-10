package dev.gyozok.estore.productsservice.commands.rest;

import java.math.BigDecimal;

public record CreateProductRestModel(
        String title,
        BigDecimal price,
        int quantity
) {
}
