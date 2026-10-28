package dev.gyozok.basics;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicReference;

public class CalculateCreditWithExecutorService {

    static void main() {
        CalculateCredit sut = new CalculateCredit();
        try {
            var credit = sut.calculateCreditWithUnboundedThreads(1L);
            System.out.println(credit);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    Credit calculateCreditWithExecutorService(Long personId) throws InterruptedException, ExecutionException {

        try (ExecutorService executor = Executors.newFixedThreadPool(5)) {
            Person person = getPerson(personId);

            Future<List<Asset>> assetsFuture = executor.submit(() -> getAssets(person));
            Future<List<Liability>> liabilitiesFuture = executor.submit(() -> getLiabilities(person));

            executor.submit(() -> importantWork());

            return calculateCredits(assetsFuture.get(), liabilitiesFuture.get());
        }
    }

    private Person getPerson(Long personId) {
        try {
            Thread.sleep(500L);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return new Person(1L, "gyozok");
    }

    private List<Asset> getAssets(Person person) {
        try {
            Thread.sleep(5000L);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        var a1 = new Asset("type1", 123.3);
        var a2 = new Asset("type2", 423.3);
        return List.of(a1, a2);
    }

    private List<Liability> getLiabilities(Person person) {
        try {
            Thread.sleep(5000L);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        var l1 = new Liability("liab type1", 999.9);
        var l2 = new Liability("liab type2", 888.8);
        return List.of(l1, l2);
    }

    private void importantWork() {
        try {
            Thread.sleep(7770L);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private Credit calculateCredits(List<Asset> assets, List<Liability> liabilities) {
        return new Credit(444.32);
    }
}

