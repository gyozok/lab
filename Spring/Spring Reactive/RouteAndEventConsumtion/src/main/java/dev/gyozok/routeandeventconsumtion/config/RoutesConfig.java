package dev.gyozok.routeandeventconsumtion.config;

import dev.gyozok.routeandeventconsumtion.model.Product;
import dev.gyozok.routeandeventconsumtion.services.ProductService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class RoutesConfig {

    private final ProductService productService;

    public RoutesConfig(ProductService productService) {
        this.productService = productService;
    }

    @Bean
    public RouterFunction<ServerResponse> router() {
        return RouterFunctions.route()
                .GET("/products", request ->
                        ServerResponse.ok()
                                .contentType(MediaType.TEXT_EVENT_STREAM)
                                .body(productService.getAll(), Product.class))
                .build();
    }

}
