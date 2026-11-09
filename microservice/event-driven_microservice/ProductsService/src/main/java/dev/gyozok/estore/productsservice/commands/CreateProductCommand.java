package dev.gyozok.estore.productsservice.commands;

import lombok.Builder;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

import java.math.BigDecimal;

@Builder
public record CreateProductCommand (
        @TargetAggregateIdentifier
        String productId,
        String title,
        BigDecimal price,
        int quantity
) {}
