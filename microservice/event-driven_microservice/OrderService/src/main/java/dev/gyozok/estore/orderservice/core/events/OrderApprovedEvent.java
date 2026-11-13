package dev.gyozok.estore.orderservice.core.events;

import dev.gyozok.estore.orderservice.core.model.OrderStatus;
import lombok.Builder;
import lombok.Data;
import lombok.Value;

@Value
public class OrderApprovedEvent {

    private final String orderId;
    private final OrderStatus orderStatus;
}
