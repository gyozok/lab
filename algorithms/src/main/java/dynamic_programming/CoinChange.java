package main.java.dynamic_programming;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class CoinChange {

    private int coinChange(int[] coins, int amount) {
        if (amount == 0) {
            return -1;
        }
        if (amount<0) {
            return 0;
        }
        int min = Integer.MAX_VALUE;
        for (int coin : coins) {
            int val = coinChange(coins, amount - coin);
            if (val > 0) {
                min = Math.min(min, val);
            } else if (val == -1) {
                min = 0;
            }
        }
        return min != Integer.MAX_VALUE ? min + 1 : 0;
    }

    private int memoization(int[] coins, int amount, Map<Integer, Integer> cache) {
        if (amount == 0) {
            return 0;
        }
        if (amount < 0) {
            return -1;
        }
        int min = Integer.MAX_VALUE;
        for (int coin : coins) {
            int remains = amount - coin;
            int val = Integer.MIN_VALUE;
            if (cache.containsKey(remains)) {
                val = cache.get(remains);
            } else {
                val = memoization(coins, remains, cache);
                cache.put(remains, val);
            }
            if (val>=0) {
                min = Math.min(min, val);
            }
        }

        return min == Integer.MAX_VALUE ? -1 : min + 1;
    }

    private int bottomUp(int[] coins, int amount) {
        int[] dp = new int[amount + 1];
        Arrays.fill(dp, amount + 1);
        dp[0] = 0;
        for (int i=1; i<=amount; i++) {
            for (int j=0; j<coins.length; j++) {
                if (coins[j] <= i) {
                    dp[i] = Math.min(dp[i], dp[i - coins[j]] + 1);
                }
            }
        }

        return dp[amount] > amount ? -1 : dp[amount];
    }

    static void main() {
        CoinChange sut = new CoinChange();

        int[] coins = {1,5,10};
        System.out.println(sut.coinChange(coins, 12));
        int[] coins2 = {2};
        System.out.println(sut.coinChange(coins2, 3));

        Map<Integer, Integer> cache = new HashMap<>();
        System.out.println(sut.memoization(coins, 12, cache));
        cache = new HashMap<>();
        System.out.println(sut.memoization(coins2, 3, cache));

        System.out.println(sut.bottomUp(coins, 12));
        System.out.println(sut.bottomUp(coins2, 3));
    }
}
