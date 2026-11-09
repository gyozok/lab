package dev.gyozok.estore.productsservice.commands;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

import java.math.BigDecimal;

public record CreateProductCommand (
        @TargetAggregateIdentifier
        String productId,
        String title,
        BigDecimal price,
        int quantity
) {
    public static class Builder {
        private String productId;
        private String title;
        private BigDecimal price;
        private int quantity;

        public Builder productId(String productId) {
            this.productId = productId;
            return this;
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder price(BigDecimal price) {
            this.price = price;
            return this;
        }

        public Builder quantity(int quantity) {
            this.quantity = quantity;
            return this;
        }

        public CreateProductCommand build() {
            return new CreateProductCommand(productId, title, price, quantity);
        }
    }
}
