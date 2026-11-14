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
import org.axonframework.deadline.DeadlineManager;
import org.axonframework.deadline.annotation.DeadlineHandler;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.modelling.saga.EndSaga;
import org.axonframework.modelling.saga.SagaEventHandler;
import org.axonframework.modelling.saga.StartSaga;
import org.axonframework.queryhandling.QueryGateway;
import org.axonframework.spring.stereotype.Saga;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Saga
@Slf4j
public class OrderSaga {

    private static final String PAYMENT_PROCESSING_TIMEOUT_DEADLINE = "payment-processing-deadline";

    @Autowired
    private transient CommandGateway commandGateway;

    @Autowired
    private transient QueryGateway queryGateway;

    @Autowired
    private transient DeadlineManager deadlineManager;

    private String scheduleId;

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
            cancelProductReservation(productReservedEvent, e.getMessage());

            return;
        }

        if (userPaymentDetails == null) {
            //Start compensating transaction
            cancelProductReservation(productReservedEvent, "Could not fetch user payment details.");

            return;
        }

        log.info("Successfully fetched user payment details for user " + userPaymentDetails.getFirstName());

        scheduleId = deadlineManager.schedule(
                Duration.of(120, ChronoUnit.SECONDS),
                PAYMENT_PROCESSING_TIMEOUT_DEADLINE,
                productReservedEvent);

        //just for testing purpose of deadline test
//        if (true) return;

        ProcessPaymentCommand processPaymentCommand = ProcessPaymentCommand.builder()
                .orderId(productReservedEvent.getOrderId())
                .paymentDetails(userPaymentDetails.getPaymentDetails())
                .paymentId(UUID.randomUUID().toString())
                .build();
        String result = null;
        try {
//            result = commandGateway.sendAndWait(processPaymentCommand, 10, TimeUnit.SECONDS);
            result = commandGateway.sendAndWait(processPaymentCommand);

        } catch (Exception e) {
            log.error(e.getMessage());
            // Start compensating transaction
            cancelProductReservation(productReservedEvent, e.getMessage());
            return;
        }

        if (result == null) {
            log.info("The ProcessPaymentCommand resulted is NULL. Initializing a compensating transaction");
            // Start compensating transaction
            cancelProductReservation(productReservedEvent, "Could not process user payment with provided payment details.");
        }
    }

    @SagaEventHandler(associationProperty = "orderId")
    public void handle(PaymentProcessedEvent paymentProcessedEvent) {

        cancelDeadline();

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

    @DeadlineHandler(deadlineName = PAYMENT_PROCESSING_TIMEOUT_DEADLINE)
    public void handlePaymentDeadline(ProductReservedEvent productReservedEvent) {
        log.info("Payment processing deadline took place. Sending a compensating command to cancel the product reservation");
        cancelProductReservation(productReservedEvent, "Payment deadline");
    }

    private void cancelProductReservation(ProductReservedEvent productReservedEvent, String reason) {
        cancelDeadline();

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

    private void cancelDeadline() {
        if (scheduleId != null) {
//            deadlineManager.cancelAll(PAYMENT_PROCESSING_TIMEOUT_DEADLINE);
            deadlineManager.cancelSchedule(PAYMENT_PROCESSING_TIMEOUT_DEADLINE, scheduleId);
            scheduleId = null;
        }
    }
}
