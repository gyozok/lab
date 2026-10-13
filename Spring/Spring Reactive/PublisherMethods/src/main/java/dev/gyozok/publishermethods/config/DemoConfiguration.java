package dev.gyozok.publishermethods.config;

import dev.gyozok.publishermethods.handler.DemoHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class DemoConfiguration {

    @Bean
    public RouterFunction<ServerResponse> router(DemoHandler demoHandler) {
        return RouterFunctions.route()
                .GET("/demo", demoHandler::demoHandler)
                .build();
    }
}
