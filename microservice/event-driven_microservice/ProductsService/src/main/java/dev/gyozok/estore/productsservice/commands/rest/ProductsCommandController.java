package dev.gyozok.estore.productsservice.commands.rest;

import dev.gyozok.estore.productsservice.commands.CreateProductCommand;
import jakarta.validation.Valid;
import org.axonframework.commandhandling.CommandExecutionException;
import org.axonframework.commandhandling.distributed.CommandDispatchException;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/products")
public class ProductsCommandController {

    private final Environment env;
    private final CommandGateway commandGateway;

    public ProductsCommandController(Environment env, CommandGateway commandGateway) {
        this.env = env;
        this.commandGateway = commandGateway;
    }

    @PostMapping
    public String createProduct(@Valid @RequestBody CreateProductRestModel request) {
        var createProductCommand = CreateProductCommand.builder()
                .productId(UUID.randomUUID().toString())
                .title(request.title())
                .price(request.price())
                .quantity(request.quantity())
                .build();

        String returnValue = null;
        try {
            returnValue = commandGateway.sendAndWait(createProductCommand);
        } catch (CommandExecutionException | CommandDispatchException e) {
            returnValue = e.getLocalizedMessage();
        }
        return returnValue;
    }

}
