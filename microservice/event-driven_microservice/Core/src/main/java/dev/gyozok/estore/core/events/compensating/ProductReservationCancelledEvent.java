package dev.gyozok.estore.core.events.compensating;

import lombok.Builder;
import lombok.Data;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Data
@Builder
public class ProductReservationCancelledEvent {
    private final String productId;
    private final int quantity;

    private final String orderId;
    private final String userId;
    private final String reason;
}
