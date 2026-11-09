package dev.gyozok.estore.productsservice.rest;

import dev.gyozok.estore.productsservice.commands.CreateProductCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping("/products")
public class ProductsController {

    @Autowired
    private Environment env;

    @PostMapping
    public String createProduct(@RequestBody CreateProductRestModel request) {
        var c = CreateProductCommand.builder()
                .productId(UUID.randomUUID().toString())
                .title(request.title())
                .price(request.price())
                .quantity(request.quantity())
                .build();

        return "HTTP POST handled" + request;
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
