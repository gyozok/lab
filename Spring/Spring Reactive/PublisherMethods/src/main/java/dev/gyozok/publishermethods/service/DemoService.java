package dev.gyozok.publishermethods.service;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DemoService {

    private Flux<String> filter(Flux<String> f1) {
        return f1
                .filter(s -> s.length() % 2 == 0);
    }

    private Flux<String> log(Flux<String> f1) {
        return f1
//            .log()
            .filter(s -> s.length() % 2 == 0)
            .log();
    }

    private Flux<String> eliminateDuplicates(Flux<String> f1) {
//        return f1.distinct();
        return f1.distinctUntilChanged();
    }

    private Flux<String> map(Flux<String> f) {
        return f.map(s -> String.valueOf(s.length()));
    }

    private Flux<String> flatMap(Flux<String> f) {
        return f.flatMap(s -> Flux.just(s.split("")));
    }

    private Mono<List<String>> collectList(Flux<String> f) {
        return f.collect(Collectors.toList());
    }

    private Mono<Integer> sumInt(Flux<String> f) {
        return f.collect(Collectors.summingInt(s -> s.length()));
    }

    private Flux<String> concat(Flux<String> f1, Flux<String> f2) {
        return f1.concatWith(f2);
    }

    private Flux<String> zipPublishers(Flux<String> f1, Flux<String> f2) {
        return f1.zipWith(f2, (x,y) -> x + "=" + y).log();
    }

    public Flux<String> getAll() {
        Flux<String> f1 = Flux.just("AAA", "BB", "C", "DDDD", "EEEEE");
        Flux<String> f2 = Flux.just("AAA", "AAA", "BB","C", "DDDD", "EEEEE", "AAA", "C");
        Flux<String> f3 = Flux.just("qwert", "qaz", "sss");

        //filter
//        return filter(f1);

        //log
//        return log(f1);

        //Eliminate duplicates
//        return eliminateDuplicates(f2);

        //map
//        return map(f1);
//        return flatMap(f1);

//        concatenate values
//        return concat(f1, f3);

        //zip publishers
        return zipPublishers(f1, f3);

    }
}
