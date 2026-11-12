package dev.gyozok.estore.productsservice.commands.rest;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;

public record CreateProductRestModel(

        @NotBlank(message = "Product Title is a required field")
        String title,

        @Min(value = 1, message = " The price should be greater than 1")
        BigDecimal price,

        @Min(value = 1, message = "Quantity cannot be lower than 1")
        @Max(value = 500, message = "Quantity cannot be greater than 500")
        int quantity
) {
}
