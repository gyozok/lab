package dev.gyozok.publishermethods.handler;

import dev.gyozok.publishermethods.service.DemoService;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
public class DemoHandler {

    private final DemoService demoService;

    public DemoHandler(DemoService demoService) {
        this.demoService = demoService;
    }

    public Mono<ServerResponse> demoHandler(ServerRequest request) {
        return ServerResponse.ok()
                .contentType(MediaType.TEXT_EVENT_STREAM)
                .body(demoService.getAll(), String.class);
    }
}
