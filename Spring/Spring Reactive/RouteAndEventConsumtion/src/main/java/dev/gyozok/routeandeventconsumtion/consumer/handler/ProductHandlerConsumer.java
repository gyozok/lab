package dev.gyozok.routeandeventconsumtion.consumer.handler;

import dev.gyozok.routeandeventconsumtion.consumer.services.ProductServices;
import dev.gyozok.routeandeventconsumtion.model.Product;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static org.springframework.web.reactive.function.server.ServerResponse.ok;

@Component
public class ProductHandlerConsumer {

    private final ProductServices productService;

    public ProductHandlerConsumer(ProductServices productService) {
        this.productService = productService;
    }

    public Mono<ServerResponse> getAl(ServerRequest request) {
        return ok()
                .contentType(MediaType.TEXT_EVENT_STREAM)
                .body(productService.getAll(), Product.class);
    }
}
