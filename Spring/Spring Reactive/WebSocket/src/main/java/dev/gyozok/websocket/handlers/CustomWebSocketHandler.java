package dev.gyozok.websocket.handlers;


import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Component
public class CustomWebSocketHandler implements WebSocketHandler {
    @Override
    public Mono<Void> handle(WebSocketSession session) {
        Flux<WebSocketMessage> f = Flux.just("A", "B", "C", "D")
                .delayElements(Duration.ofSeconds(2L))
                .map(e -> session.textMessage(e));
        return session.send(f);
    }
}
