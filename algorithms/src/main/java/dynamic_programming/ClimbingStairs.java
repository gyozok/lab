package main.java.dynamic_programming;

import java.util.HashMap;
import java.util.Map;

public class ClimbingStairs {

    // In case of recursive version the problem space is too big, running too long
    private int recursive(int n) {
        if (n <= 1) {
            return 1;
        }
        int sum = recursive(n-1) + recursive(n-2);
        return sum;
    }

    //we stepfrom 0 towards nth stair. We are at the actualStair
    private int recursive2(int n, int actualStair) {
        if (actualStair >= n) {
            return actualStair == n ? 1 : 0;
        }
        return recursive2(n, actualStair + 1) + recursive2(n, actualStair + 2);
    }

    //memoization
    private int memoization(int n, Map<Integer, Integer> cache) {
        if (n <= 1) {
            return 1;
        }
        if (cache.containsKey(n)) {
            return cache.get(n);
        }
        int sum = memoization(n-1,  cache) + memoization(n-2, cache);
        cache.put(n, sum);
        return sum;
    }

    private int bottomUp(int n) {
        Map<Integer, Integer> cache = new HashMap<>();
        cache.put(0,1);
        cache.put(1,1);
        int i=2;
        while (i<=n) {
            int sum = cache.get(i-1) + cache.get(i-2);
            cache.put(i, sum);
            i++;
        }
        return cache.get(n);
    }

    private int bottomUpOptimized(int n) {
        int[] dp = new int[2];
        dp[0] = 1;
        dp[1] = 1;
        int i=2;
        while (i<=n) {
            int tmp = dp[1];
            dp[1] = dp[0] + tmp;
            dp[0] = tmp;
            i++;
        }
        return dp[1];
    }

    static void main() {
        ClimbingStairs sut = new ClimbingStairs();

        System.out.println(sut.recursive(15));
        System.out.println(sut.recursive2(15, 0));

        Map<Integer, Integer> cache = new HashMap<>();
        System.out.println(sut.memoization(15, cache));
        cache = new HashMap<>();
        System.out.println(sut.memoization(37, cache));

        System.out.println(sut.bottomUp(37));
        System.out.println(sut.bottomUpOptimized(37));
    }
}
