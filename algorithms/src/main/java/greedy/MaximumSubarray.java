package main.java.greedy;

public class MaximumSubarray {

    public int maxSubArray(int[] nums) {
        return dfs(nums, 0, false);
    }

    private int dfs(int[] nums, int index, boolean isStarted) {
            if (index == nums.length - 1) {
                return isStarted ? Math.max(0, nums[index]) : nums[index];
            }
            if (isStarted) {
                return Math.max(0, dfs(nums, index + 1, true));
            }
            return Math.max(dfs(nums, index + 1, false),
                    dfs(nums, index + 1, true) + nums[index]);
    }

    public int memoization(int[] nums) {
        Integer[][] cache = new Integer[nums.length][2];
        return memoizationDfs(nums, 0, false, cache);
    }

    private int memoizationDfs(int[] nums, int index, boolean isStarted, Integer[][] cache) {
        if (index == nums.length - 1) {
            return isStarted ? Math.max(nums[index], 0) : nums[index];
        }

        int f = isStarted ? 1 : 0;
        if (cache[index][f] != null) {
            return cache[index][f];
        }

        cache[index][f] = isStarted ? Math.max(0, nums[index] + memoizationDfs(nums, index+1, true, cache))
                : Math.max(nums[index] + memoizationDfs(nums, index + 1, true, cache),
                memoizationDfs(nums, index+1, false, cache));

        return cache[index][f];
    }

    public int bottomUp(int[] nums) {
        int n = nums.length;
        int[][] dp = new int[n][2];

        dp[n-1][1] = nums[n-1];
        dp[n-1][0] = nums[n-1];

        for (int i = n -2; i >= 0; i--) {
            dp[i][1] = Math.max(nums[i], dp[i+1][1] + nums[i]);
            dp[i][0] = Math.max(dp[i][1], dp[i+1][0] );
        }

        return dp[0][0];
    }

    static void main() {
        MaximumSubarray sut = new MaximumSubarray();

        int[] nums = {2,-3,4,-2,2,1,-1,4};
        System.out.println(sut.maxSubArray(nums));
        int[] nums2 = {-1};
        System.out.println(sut.maxSubArray(nums2));

        System.out.println(sut.memoization(nums));
        System.out.println(sut.memoization(nums2));

        System.out.println(sut.bottomUp(nums));
        System.out.println(sut.bottomUp(nums2));
    }
}
