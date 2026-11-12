package dev.gyozok.estore.orderservice.saga;

import dev.gyozok.estore.core.commands.ReserveProductCommand;
import dev.gyozok.estore.core.events.ProductReservedEvent;
import dev.gyozok.estore.core.model.User;
import dev.gyozok.estore.core.query.FetchUserPaymentDetailsQuery;
import dev.gyozok.estore.orderservice.core.events.OrderCreatedEvent;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.CommandCallback;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.modelling.saga.SagaEventHandler;
import org.axonframework.modelling.saga.StartSaga;
import org.axonframework.queryhandling.QueryGateway;
import org.axonframework.spring.stereotype.Saga;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.CompletableFuture;

@Saga
@Slf4j
public class OrderSaga {

    @Autowired
    private transient CommandGateway commandGateway;

    @Autowired
    private transient QueryGateway queryGateway;

//    public OrderSaga(QueryGateway queryGateway, CommandGateway commandGateway) {
//        this.queryGateway = queryGateway;
//        this.commandGateway = commandGateway;
//    }

    @StartSaga
    @SagaEventHandler(associationProperty = "orderId")
    public void handle(OrderCreatedEvent orderCreatedEvent) {
        ReserveProductCommand reserveProductCommand = ReserveProductCommand.builder()
                .orderId(orderCreatedEvent.getOrderId())
                .productId(orderCreatedEvent.getProductId())
                .quantity(orderCreatedEvent.getQuantity())
                .userId(orderCreatedEvent.getUserId())
                .build();

        log.info("OrderCreatedEvent handled for orderId: " + reserveProductCommand.getOrderId()
        + " productId: " + reserveProductCommand.getProductId());

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

    @SagaEventHandler(associationProperty = "orderId")
    public void handle(ProductReservedEvent productReservedEvent) {
        //process user payment
        log.info("ProductReservedEvent handled for orderId: " + productReservedEvent.getOrderId()
                + " productId: " + productReservedEvent.getProductId());

        FetchUserPaymentDetailsQuery fetchUserPaymentDetailsQuery = new FetchUserPaymentDetailsQuery(productReservedEvent.getUserId());

        User userPaymentDetails = null;
        try {
            CompletableFuture<User> futureUser = queryGateway.query(fetchUserPaymentDetailsQuery, ResponseTypes.instanceOf(User.class));
            userPaymentDetails = futureUser.join();
            log.info("User details: " + userPaymentDetails);
        } catch (Exception e) {
            log.error(e.getMessage());
            // Start a compensating transaction

            return;
        }

        if (userPaymentDetails == null) {
            //Start compensating transaction
            return;
        }

        log.info("Successfully fetched user payment details for user " + userPaymentDetails.getFirstName());

    }
}
