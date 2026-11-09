package dev.gyozok.estore.productsservice.rest;

import dev.gyozok.estore.productsservice.commands.CreateProductCommand;
import org.axonframework.commandhandling.CommandExecutionException;
import org.axonframework.commandhandling.distributed.CommandDispatchException;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping("/products")
public class ProductsController {

    private final Environment env;
    private final CommandGateway commandGateway;

    public ProductsController(Environment env, CommandGateway commandGateway) {
        this.env = env;
        this.commandGateway = commandGateway;
    }

    @PostMapping
    public String createProduct(@RequestBody CreateProductRestModel request) {
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

    @GetMapping
    public String getProduct() {
        return "HTTP GET Handled " +env.getProperty("local.server.port");
    }

    @PutMapping
    public String updateProduct() {
        return "HTTP PUT Handled";
    }

    @DeleteMapping
    public String deleteProduct() {
        return "HTTP DELETE Handled";
    }
}
