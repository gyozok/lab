package dev.gyozok.routeandeventconsumtion.consumer.config;

import dev.gyozok.routeandeventconsumtion.consumer.handler.ProductHandlerConsumer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class RouteConfig {

    @Bean
    public RouterFunction<ServerResponse> routerConsumer(ProductHandlerConsumer productHandler) {
        return route()
                .GET("/all", productHandler::getAl)
                .build();
    }
}
