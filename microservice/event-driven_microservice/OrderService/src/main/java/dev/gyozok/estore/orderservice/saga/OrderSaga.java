package dev.gyozok.estore.orderservice.saga;

import dev.gyozok.estore.core.commands.ProcessPaymentCommand;
import dev.gyozok.estore.core.commands.ReserveProductCommand;
import dev.gyozok.estore.core.commands.compensating.CancelProductReservationCommand;
import dev.gyozok.estore.core.events.PaymentProcessedEvent;
import dev.gyozok.estore.core.events.ProductReservedEvent;
import dev.gyozok.estore.core.events.compensating.ProductReservationCancelledEvent;
import dev.gyozok.estore.core.model.User;
import dev.gyozok.estore.core.query.FetchUserPaymentDetailsQuery;
import dev.gyozok.estore.orderservice.command.commands.ApproveOrderCommand;
import dev.gyozok.estore.orderservice.command.commands.compensating.RejectOrderCommand;
import dev.gyozok.estore.orderservice.core.events.OrderApprovedEvent;
import dev.gyozok.estore.orderservice.core.events.OrderCreatedEvent;
import dev.gyozok.estore.orderservice.core.events.OrderRejectedEvent;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.modelling.saga.EndSaga;
import org.axonframework.modelling.saga.SagaEventHandler;
import org.axonframework.modelling.saga.StartSaga;
import org.axonframework.queryhandling.QueryGateway;
import org.axonframework.spring.stereotype.Saga;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

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
            cancelProductResrervation(productReservedEvent, e.getMessage());

            return;
        }

        if (userPaymentDetails == null) {
            //Start compensating transaction
            cancelProductResrervation(productReservedEvent, "Could not fetch user payment details.");

            return;
        }

        log.info("Successfully fetched user payment details for user " + userPaymentDetails.getFirstName());

        ProcessPaymentCommand processPaymentCommand = ProcessPaymentCommand.builder()
                .orderId(productReservedEvent.getOrderId())
                .paymentDetails(userPaymentDetails.getPaymentDetails())
                .paymentId(UUID.randomUUID().toString())
                .build();
        String result = null;
        try {
            result = commandGateway.sendAndWait(processPaymentCommand, 10, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error(e.getMessage());
            // Start compensating transaction
            cancelProductResrervation(productReservedEvent, e.getMessage());
            return;
        }

        if (result == null) {
            log.info("The ProcessPaymentCommand resulted is NULL. Initializing a compensating transaction");
            // Start compensating transaction
            cancelProductResrervation(productReservedEvent, "Could not process user payment with provided payment details.");
        }
    }

    @SagaEventHandler(associationProperty = "orderId")
    public void handle(PaymentProcessedEvent paymentProcessedEvent) {
        //Send ApproveOrderCommand
        ApproveOrderCommand approveOrderCommand = new ApproveOrderCommand(paymentProcessedEvent.getOrderId());

        commandGateway.send(approveOrderCommand);
    }

    @EndSaga
    @SagaEventHandler(associationProperty = "orderId")
    public void handle(OrderApprovedEvent orderApprovedEvent) {
        log.info("Order is approved, Order Saga is complete for orderId: " + orderApprovedEvent.getOrderId());
//        SagaLifecycle.end();
    }

    @SagaEventHandler(associationProperty = "orderId")
    public void handle(ProductReservationCancelledEvent productReservationCancelledEvent) {
        // Create and send a RejectOrderCommand
        RejectOrderCommand rejectOrderCommand = new RejectOrderCommand(
                productReservationCancelledEvent.getOrderId(),
                productReservationCancelledEvent.getReason()
        );

        commandGateway.send(rejectOrderCommand);
    }

    @SagaEventHandler(associationProperty = "orderId")
    public void on(OrderRejectedEvent orderRejectedEvent) {
        log.info("Successfully rejected order with id: " + orderRejectedEvent.getOrderId());
    }

    private void cancelProductResrervation(ProductReservedEvent productReservedEvent, String reason) {

        CancelProductReservationCommand cancelProductReservationCommand =
                CancelProductReservationCommand.builder()
                        .orderId(productReservedEvent.getOrderId())
                        .productId(productReservedEvent.getProductId())
                        .quantity(productReservedEvent.getQuantity())
                        .userId(productReservedEvent.getUserId())
                        .reason(reason)
                        .build();

        commandGateway.send(cancelProductReservationCommand);
    }
}
