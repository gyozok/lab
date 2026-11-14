package dev.gyozok.estore.orderservice.query.subscriptionqueries;

import lombok.Value;

@Value
public class FindOrderQuery {
    private final String orderId;
}
