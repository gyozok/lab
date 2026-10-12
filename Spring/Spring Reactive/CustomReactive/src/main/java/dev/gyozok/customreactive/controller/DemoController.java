package dev.gyozok.customreactive.controller;

import dev.gyozok.customreactive.publisher.DemoPublisher;
import dev.gyozok.customreactive.subscribers.DemoSubscriber;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.UUID;
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
        dp.subscribe(new DemoSubscriber());

        //programmatic way to define a publisher (Flux)
        Flux<String> f2 = Flux.create(
            s -> {
                for (int i=1; i<=10; i++) {
                    s.next(UUID.randomUUID().toString());
                }
                s.complete();
            }
        );
        f2
                .log()
                .subscribe(c -> System.out.println(c)); //doesn't use backpressure

        f2.log().subscribe(new Subscriber<String>() {
            private Subscription subscription;
            private int i=0; //call the request after every nth time

            @Override
            public void onSubscribe(Subscription s) {
                this.subscription = s;
                s.request(2);
            }

            @Override
            public void onNext(String s) {
                System.out.println("custom: " + s);
                i++;
                if (i % 2 == 0) {
                    subscription.request(2);
                }
            }

            @Override
            public void onError(Throwable t) {

            }

            @Override
            public void onComplete() {
                System.out.println("Completed");
            }
        });
    }
}
