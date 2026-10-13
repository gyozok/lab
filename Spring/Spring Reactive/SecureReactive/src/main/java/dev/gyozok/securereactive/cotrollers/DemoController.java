package dev.gyozok.securereactive.cotrollers;

import org.springframework.http.MediaType;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;


@RestController
public class DemoController {

    @GetMapping(value = "/demo", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Mono<String> demo() {
//        return ReactiveSecurityContextHolder.getContext()
//                .map(sc -> sc.getAuthentication())
//                .doOnNext(auth -> System.out.println("Authenticated: " + auth))
//                .map(auth -> "demo result")
//                .switchIfEmpty(Mono.just("demo result"));
       return Mono.just("demo result");
    }

    @GetMapping("/hello")
    public Mono<String> hello() {

        return Mono.just("hello");
    }
}
