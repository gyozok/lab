package dev.gyozok.virtualthread;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class GeneratePhrase {

    public String generatePhrase() throws ExecutionException,
            InterruptedException {
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            Future<String> adjectiveFuture =
                    executor.submit(this::fetchAdjective);
            Future<String> nounFuture = executor.submit(this::fetchNoun);
            String adjective = adjectiveFuture.get();
            String noun = nounFuture.get();
            return adjective + " " + noun;
        }
    }
    private String fetchAdjective() {
// Fetch adjective from an API
        return "beautiful";
    }
    private String fetchNoun() {
// Fetch noun from an API
        return "sunset";
    }

    static void main() throws ExecutionException, InterruptedException {
        GeneratePhrase sut = new GeneratePhrase();
        System.out.println(sut.generatePhrase());
    }
}
