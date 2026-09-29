package main.java.dynamic_programming;

import java.util.HashMap;
import java.util.Map;

public class Fibonacci1D {

    //memoization ot Top-Down approach
    private int fibonacci(int n, Map<Integer, Integer> cache) {
        if (n <= 1) {
            return  n;
        }
        if (cache.containsKey(n)) {
            return cache.get(n);
        } else {
            int sum = fibonacci(n-1, cache) + fibonacci(n-2, cache);
            cache.put(n, sum);
            return sum;
        }
    }

    //BottomUp approach
    private int bottomUp(int n) {
        if (n<2) {
            return n;
        }
        int[] dp = new int[n+1];
        dp[0] = 0;
        dp[1] = 1;

        int i=2;
        while (i <= n) {
            dp[i] = dp[i-1] + dp[i-2];
            i++;
        }
        return dp[n];
    }

    private int bottomUpOptimized(int n) {
        if (n<2) {
            return n;
        }
        int[] dp = new int[2];
        dp[0] = 0;
        dp[1] = 1;

        int i=2;
        while (i <= n) {
            int tmp = dp[1];
            dp[1] = dp[0] + tmp;
            dp[0] = tmp;
            i++;
        }
        return dp[1];
    }

    static void main() {
        Fibonacci1D sut = new Fibonacci1D();
        Map<Integer, Integer> cache = new HashMap<>();
        System.out.println(sut.fibonacci(8, cache));

        System.out.println(sut.bottomUp(8));
        System.out.println(sut.bottomUpOptimized(8));
    }

}
