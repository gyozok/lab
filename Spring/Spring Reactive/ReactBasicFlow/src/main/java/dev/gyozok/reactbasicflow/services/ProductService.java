package dev.gyozok.reactbasicflow.services;

import dev.gyozok.reactbasicflow.model.Product;
import dev.gyozok.reactbasicflow.repositories.ProductRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.time.Duration;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Flux<Product> getProducts() {
        return productRepository.findAll()
                .delayElements(Duration.ofSeconds(5L)); //for any element add a sleep duration
    }
}
