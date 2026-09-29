package main.java.dynamic_programming;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class HouseRobber2 {

    private int recursion(int[] nums, int hNum, boolean first) {
        int n = nums.length;
        if (hNum>= n  ||
                ((hNum == n-1) && first) )  {
                return 0;
        }
        return Math.max(recursion(nums, hNum + 1, first), nums[hNum] + recursion(nums, hNum + 2, first));
    }

    private int memoization(int[] nums, int hNum, boolean first, Map<Integer, Map<Boolean, Integer>> cache) {
        int n = nums.length;
        if (hNum >= n || (hNum == n-1 && first) ){
                return 0;
        }
        if (cache.getOrDefault(hNum, Map.of()).containsKey(first)) {
            return cache.get(hNum).get(first);
        }
        int money = Math.max(
                memoization(nums, hNum + 1, first, cache),
                nums[hNum] + memoization(nums, hNum + 2, first|| hNum == 0, cache)
                );
        Map<Boolean, Integer> m = cache.getOrDefault(hNum, new HashMap<>());
        m.put(first, money);
        cache.put(hNum, m);
        return cache.get(hNum).get(first);
    }

    private int bottomUp(int[] nums) {
        if (nums.length == 0) return 0;
        if (nums.length == 1) return nums[0];
        int[] dp = new int[nums.length];
        dp[0] = nums[0];
        dp[1] = Math.max(nums[0], nums[1]);

        for (int i=2; i < nums.length; i++) {
            dp[i] = Math.max(
              dp[i-1],
              nums[i] + dp[i-2]
            );
        }

        return dp[nums.length - 1];
    }

    static void main() {
        HouseRobber2 sut = new HouseRobber2();
        int[] nums = {2,9,8,3,6};

        System.out.println(Math.max(
                sut.recursion(nums, 0, true),
                sut.recursion(nums, 1, false )
                ));

        Map<Integer, Map<Boolean, Integer>> cache = new HashMap<>();
        System.out.println(Math.max(
                sut.memoization(nums, 0, true, cache),
                sut.memoization(nums, 1, false, cache)
                ));

        System.out.println(
                Math.max(sut.bottomUp(Arrays.copyOfRange(nums, 1, nums.length)),
                sut.bottomUp(Arrays.copyOfRange(nums, 0, nums.length - 1))
        ));
    }
}
