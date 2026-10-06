package main.java.greedy;

import java.util.HashMap;
import java.util.Map;

public class JumpGame {

    public boolean canJump(int[] nums) {
        return dfs(nums, 0);
    }

    private boolean dfs(int[] nums, int index) {
        if (index>=nums.length) {
            return false;
        }
        if (index == nums.length - 1) {
            return true;
        }
        int n = nums[index];
        for (int i=1; i<=n; i++) {
            boolean res = dfs(nums, index + i);
            if (res) return true;
        }

        return false;
    }

    public boolean memoization(int[] nums) {
        Map<Integer, Boolean> cache = new HashMap<>();
        return memoizationDfs(nums, 0, cache);
    }

    private boolean memoizationDfs(int[] nums, int index, Map<Integer, Boolean> cache) {
        if (cache.containsKey(index)) {
            return cache.get(index);
        }
        if (index == nums.length - 1) {
            cache.put(index, true);
            return true;
        }
        if (nums[index] == 0) {
            cache.put(index, false);
            return false;
        }

        int end = Math.min(nums.length, index + nums[index] + 1);
        for (int i = index + 1; i<end; i++) {
            if (memoizationDfs(nums, i, cache)) {
                cache.put(index, true);
                return true;
            }
        }
        cache.put(index, false);
        return false;
    }

    public boolean bottomUp(int[] nums) {
        int n = nums.length;
        boolean[] dp = new boolean[n];
        dp[n-1] = true;

        for (int i=n-2; i>=0; i--) {
            int end = Math.min(n-1, i + nums[i]);
            for (int j = i; j<=end; j++) {
                if (dp[j]) {
                    dp[i] = true;
                    break;
                }
            }
        }

        return dp[0];
    }

    public boolean greedy(int[] nums) {
        int goal = nums.length - 1;
        for (int i=nums.length -2; i>=0; i--) {
            if (i + nums[i] >= goal) {
                goal = i;
            }
        }
        return goal == 0;
    }

    static void main() {
        JumpGame sut = new JumpGame();

        int[] nums = {1,2,0,1,0};
        System.out.println(sut.canJump(nums));
        int[] nums2 = {1,2,1,0,1};
        System.out.println(sut.canJump(nums2));

        System.out.println(sut.memoization(nums));
        System.out.println(sut.memoization(nums2));

        System.out.println(sut.bottomUp(nums));
        System.out.println(sut.bottomUp(nums2));

        System.out.println(sut.greedy(nums));
        System.out.println(sut.greedy(nums2));
    }
}
