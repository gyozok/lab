package dev.gyozok.virtualthread.semaphore;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ResourceAwareRateLimiting {
    private static final HttpClient CLIENT = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    private static final int MAX_PARALLEL = 10; //RATE LIMIT
    private static final Semaphore gate = new Semaphore(MAX_PARALLEL);

    private static final String API_URL = "https://api.chucknorris.io/jokes/random";

    static void main() {
        Instant start = Instant.now();

        List<String> jokes = fetchJokes(150);

        long ms = Duration.between(start, Instant.now()).toMillis();
        System.out.printf("Fetched %d jokes in %d ms (avg %d ms)\n", jokes.size(), ms, ms/jokes.size());
        jokes.stream()
                .limit(3)
                .forEach(System.out::println);
    }

    private static List<String> fetchJokes(int n)  {
        try (ExecutorService pool = Executors.newVirtualThreadPerTaskExecutor()) {
            List<Future<String>> futures = IntStream.range(0, n)
                    .mapToObj(i -> pool.submit(ResourceAwareRateLimiting::fetchJoke))
                    .collect(Collectors.toList());
            return futures.stream()
                    .map(ResourceAwareRateLimiting::join)
                    .toList();
        }
    }

    private static String fetchJoke() throws IOException, InterruptedException {
        HttpRequest req = HttpRequest.newBuilder(URI.create(API_URL))
                .GET()
                .timeout(Duration.ofSeconds(30))
                .build();
        try {
            gate.acquire();
            HttpResponse<String> response = CLIENT.send(req, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                throw new RuntimeException("API error " + response.statusCode());
            }
            return parseJoke(response.body());
        } finally {
            gate.release();
        }
    }

    private static String parseJoke(String json) {
        int startIndex = json.indexOf("\"value\":\"") + 9;
        int endIndex = json.indexOf("\"", startIndex);
        return json.substring(startIndex, endIndex).replace("\\\"", "\"");
    }

    private static <T> T join(Future<T> f) {
        try {
            return f.get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new CompletionException(e);
        } catch (ExecutionException e) {
            throw new CompletionException(e.getCause());
        }
    }
}
