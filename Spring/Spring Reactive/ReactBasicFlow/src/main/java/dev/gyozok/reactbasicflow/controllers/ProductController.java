package dev.gyozok.reactbasicflow.controllers;

import dev.gyozok.reactbasicflow.model.Product;
import dev.gyozok.reactbasicflow.services.ProductService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping(value = "/products", produces= MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Product> getProduct() {
        return productService.getProducts();
    }
}
