package dev.gyozok.routeandeventconsumtion.consumer.proxy;

import dev.gyozok.routeandeventconsumtion.consumer.exceptions.ProductRetrieveException;
import dev.gyozok.routeandeventconsumtion.model.Product;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import reactor.core.publisher.Flux;
import reactor.util.retry.Retry;

import java.time.Duration;

@Component
public class ProductProxy {

    private final WebClient webClient;

    public ProductProxy(WebClient webClient) {
        this.webClient = webClient;
    }

    public Flux<Product> getAllProducts() {

        Retry r = Retry.backoff(3, Duration.ofMillis(100L));
        var p = new Product();
        p.setName("Default");
        return webClient
                .get().uri("/products")
                .exchangeToFlux(response -> response.bodyToFlux(Product.class))
//                .onErrorResume(
//                        exception ->
//                                Flux.just(p)
//                );
//                .onErrorResume(WebClientRequestException.class, e -> Flux.just(p));
//                .onErrorResume(e -> e instanceof WebClientRequestException, e -> Flux.just(p));
//                .onErrorReturn(p);
//                .onErrorMap(e -> new ProductRetrieveException());
//                .doOnNext(n -> {
//                    if (n.getName() == null) throw new ProductRetrieveException();
//                })
//                .onErrorContinue((e, o) -> System.out.println(e.getMessage()));
//                .retryWhen(r);
                .retry(900L);
    }
}
