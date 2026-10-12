package dev.gyozok.customreactive.controller;

import dev.gyozok.customreactive.publisher.DemoPublisher;
import dev.gyozok.customreactive.subscribers.DemoSubscriber;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.stream.Stream;

@RestController
public class DemoController {

    @GetMapping("/demo")
    public void demo() {
        //Stream API vs Reactor
        Flux<Integer> f1 = Flux.just(1,2,3,4,5,6,7); // publisher
        f1.subscribe(c -> System.out.println(c));

        Stream<Integer> s1 = Stream.of(1,2,3,4,5,6,7);
        s1.forEach(System.out::println);

        //Demo publisher
        DemoPublisher dp = new DemoPublisher(List.of(1,2,3,4,5,6,7));
        dp.subscribe(new DemoSubscriber() {

        });
    }
}
