package dev.gyozok.estore.orderservice.saga;

import dev.gyozok.estore.core.commands.ReserveProductCommand;
import dev.gyozok.estore.orderservice.core.events.OrderCreatedEvent;
import org.axonframework.commandhandling.CommandCallback;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.modelling.saga.SagaEventHandler;
import org.axonframework.modelling.saga.StartSaga;
import org.axonframework.spring.stereotype.Saga;
import org.springframework.beans.factory.annotation.Autowired;

@Saga
public class OrderSaga {

    @Autowired
    private transient CommandGateway commandGateway;

    @StartSaga
    @SagaEventHandler(associationProperty = "orderId")
    public void handle(OrderCreatedEvent orderCreatedEvent) {
        ReserveProductCommand reserveProductCommand = ReserveProductCommand.builder()
                .orderId(orderCreatedEvent.getOrderId())
                .productId(orderCreatedEvent.getProductId())
                .quantity(orderCreatedEvent.getQuantity())
                .userId(orderCreatedEvent.getUserId())
                .build();

        //send the command and use a callback
        commandGateway.send(
                reserveProductCommand,
//                Use lambda insteaad of this anonymous class
//                new CommandCallback<ReserveProductCommand, Object>() {
//                    @Override
//                    public void onResult()
//                }
                (commandMessage, commandResultMessage) -> {
                    if (commandResultMessage.isExceptional()) {
                        //unsuccessfull execution
                        //Start a compensating transaction
                    }
                }
        );
    }
}
