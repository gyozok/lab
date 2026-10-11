package dev.gyozok.routeandeventconsumtion.consumer.services;

import dev.gyozok.routeandeventconsumtion.consumer.proxy.ProductProxy;
import dev.gyozok.routeandeventconsumtion.model.Product;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public class ProductServices {

    private final ProductProxy productProxy;

    public ProductServices(ProductProxy productProxy) {
        this.productProxy = productProxy;
    }

    public Flux<Product> getAll() {
        return productProxy.getAllProducts();
    }
}
