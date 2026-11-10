package dev.gyozok.estore.productsservice.core.events;

import dev.gyozok.estore.productsservice.commands.CreateProductCommand;

import java.math.BigDecimal;

public record ProductCreatedEvent (
        String productId,
        String title,
        BigDecimal price,
        int quantity
){
    public static ProductCreatedEvent fromCommand(CreateProductCommand command) {
        return new ProductCreatedEvent(
          command.getProductId(),
          command.getTitle(),
          command.getPrice(),
          command.getQuantity()
        );
    }
}
