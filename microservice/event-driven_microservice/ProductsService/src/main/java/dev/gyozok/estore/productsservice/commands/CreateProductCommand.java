package dev.gyozok.estore.productsservice.commands;

import lombok.Builder;
import lombok.Data;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

import java.math.BigDecimal;

@Builder
@Data
public class CreateProductCommand {
        @TargetAggregateIdentifier
        private String productId;
        private String title;
        private BigDecimal price;
        private int quantity;
}
