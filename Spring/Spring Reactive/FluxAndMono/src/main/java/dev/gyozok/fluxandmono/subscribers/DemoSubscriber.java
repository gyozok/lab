package dev.gyozok.fluxandmono.subscribers;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.concurrent.Flow;

public class DemoSubscriber implements Subscriber<Integer> {
    private Subscription subscription;

    @Override
    public void onSubscribe(Subscription s) {
        System.out.println("Subscribed");
        this.subscription = s;
        s.request(1);
    }

    @Override
    public void onNext(Integer integer) {
        System.out.println("On next: " + integer);
        subscription.request(1);
    }

    @Override
    public void onError(Throwable t) {
        System.out.println("onError: " + t);
    }

    @Override
    public void onComplete() {
        System.out.println("onComplete");
    }
}
