package dev.gyozok.customreactive.subscribers;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;


public class DemoSubscriber implements Subscriber<Integer> {
    private Subscription subscription;

    @Override
    public void onSubscribe(Subscription s) {
        this.subscription = s;
        s.request(1);
    }

    @Override
    public void onNext(Integer integer) {
        System.out.println("custom: " + integer);
        subscription.request(1);
    }

    @Override
    public void onError(Throwable t) {

    }

    @Override
    public void onComplete() {
        System.out.println("Completed");
    }
}
