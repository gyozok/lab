package dev.gyozok.websocket.handlers;


import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.time.Duration;
import java.util.UUID;

@Component
public class CustomWebSocketHandler implements WebSocketHandler {

    private final Sinks.Many<String> sink;

    public CustomWebSocketHandler(Sinks.Many<String> sink) {
        this.sink = sink;
    }

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        //static message
//        Flux<WebSocketMessage> f = Flux.just("A", "B", "C", "D")
//                .delayElements(Duration.ofSeconds(2L))
//                .map(e -> session.textMessage(e));

        // dynamic message creation
//        Flux<WebSocketMessage> f = Flux.interval(Duration.ofSeconds(1))
//                .map(tick -> UUID.randomUUID().toString()) // Generates a new UUID every second
//                .map(session::textMessage);

        //use Sinks
        Flux<WebSocketMessage> f = sink
                .asFlux()
                .map(event -> session.textMessage(event));
        return session.send(f);
    }
}
