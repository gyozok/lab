package dev.gyozok.routeandeventconsumtion.services;

import dev.gyozok.routeandeventconsumtion.model.Product;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.List;

@Service
public class ProductService {

    public Flux<Product> getAll() {
        var p1 = new Product();
        p1.setName("Bear");
        var p2 = new Product();
        p2.setName("Chcocolate");

        Flux<Product> products = Flux
                .fromStream(List.of(p1,p2).stream())
                .delayElements(Duration.ofSeconds(3));

        return products;
    }
}
