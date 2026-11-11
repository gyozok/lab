package dev.gyozok.estore.orderservice.command.rest;

import dev.gyozok.estore.orderservice.command.commands.CreatedOrderCommand;
import dev.gyozok.estore.orderservice.core.model.OrderStatus;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/orders")
public class OrdersCommandController {

    private final CommandGateway commandGateway;

    public OrdersCommandController(CommandGateway commandGateway) {
        this.commandGateway = commandGateway;
    }

    @PostMapping
    public String createOrder(@RequestBody OrderRestModel request) {
        CreatedOrderCommand createdOrderCommand = new CreatedOrderCommand(
                UUID.randomUUID().toString(),
                "27b95829-4f3f-4ddf-8983-151ba010e35b",
                request.getProductId(),
                request.getQuantity(),
                request.getAddressId(),
                OrderStatus.CREATED
        );
        String returnValue = commandGateway.sendAndWait(createdOrderCommand);

        return returnValue;
    }
}
