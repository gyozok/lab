package dev.gyozok.fluxandmono.controller;

import dev.gyozok.fluxandmono.subscribers.DemoSubscriber;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
public class DemoController {

    @GetMapping("/demo")
    public void demo() {
        Flux<Integer> f1 = Flux.just(1,2,3,4,5,6);
//        var f2 = Flux.fromStream(Stream.of(1,2,3,4,5,6,7));
//        var f3 = Flux.fromIterable(Set.of(1,2,3,4,5,6,7));
//        var f4 =  Flux.fromArray(Test.values());

//        var m = Mono.just(1);

        f1
//                .doOnNext(n -> System.out.println(n))
                .doOnNext(c -> {
                    if (c==3) throw new RuntimeException("Nooooooo");
                })
//                .subscribe(c -> System.out.println(c));
                .subscribe(new DemoSubscriber());
    }
}

enum Test {
    TEST1,
    TEST2,
    TEST3
}