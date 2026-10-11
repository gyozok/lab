package dev.gyozok.routeandeventconsumtion.config;

import dev.gyozok.routeandeventconsumtion.handlers.ProductHandler;
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

    @Bean
    public RouterFunction<ServerResponse> router(ProductHandler productHandler) {
        return RouterFunctions.route()
                .GET("/products", productHandler::getAll)
                .build();
    }

}
