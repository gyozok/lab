package dev.gyozok.estore.orderservice.command.rest;

import lombok.Data;

@Data
public class OrderRestModel {
    private final String productId;
    private final int quantity;
    private final String addressId;
}
