package dev.gyozok.estore.orderservice.command.rest;

import dev.gyozok.estore.orderservice.command.commands.CreatedOrderCommand;
import dev.gyozok.estore.orderservice.core.model.OrderStatus;
import dev.gyozok.estore.orderservice.core.model.OrderSummary;
import dev.gyozok.estore.orderservice.query.subscriptionqueries.FindOrderQuery;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.axonframework.queryhandling.SubscriptionQueryResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/orders")
public class OrdersCommandController {

    private final CommandGateway commandGateway;
    private final QueryGateway queryGateway;


    public OrdersCommandController(CommandGateway commandGateway, QueryGateway queryGateway) {
        this.commandGateway = commandGateway;
        this.queryGateway = queryGateway;
    }

    @PostMapping
    public OrderSummary createOrder(@RequestBody OrderRestModel request) {
        String userId = "27b95829-4f3f-4ddf-8983-151ba010e35b";
        CreatedOrderCommand createdOrderCommand = new CreatedOrderCommand(
                UUID.randomUUID().toString(),
                userId,
                request.getProductId(),
                request.getQuantity(),
                request.getAddressId(),
                OrderStatus.CREATED
        );

        SubscriptionQueryResult<OrderSummary, OrderSummary> queryResult =
        queryGateway.subscriptionQuery(
                new FindOrderQuery(createdOrderCommand.getOrderId()),
                ResponseTypes.instanceOf(OrderSummary.class),
                ResponseTypes.instanceOf(OrderSummary.class)
        );
        try {
            commandGateway.sendAndWait(createdOrderCommand);
            return queryResult.updates().blockFirst();
        } finally {
            queryResult.close();
        }
    }
}
