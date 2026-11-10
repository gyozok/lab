package dev.gyozok.estore.productsservice.core.errorhandling;

import lombok.Data;

import java.time.Instant;

@Data
public class ErrorMessage {

    private final Instant timestamp;
    private final String message;
}
