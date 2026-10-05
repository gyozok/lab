package main.java.dynamic_programming;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LongestIncreasingSubsequence {

    public int lengthOfLIS(int[] nums) {
        return dfs(nums, 0, new ArrayList<Integer>());
    }

    private int dfs(int[] nums, int index, List<Integer> list) {
        if (index == nums.length) {
            return list.size();
        }
        int length = Integer.MIN_VALUE;
        if (list.isEmpty() || list.getLast()<nums[index]) {
            list.add(nums[index]);
            int res = dfs(nums, index+1, list);
            length = Math.max(length, res);
            list.removeLast();
        }
        int res = dfs(nums, index+1, list);
        length = Math.max(length, res);

        return length;
    }

    public int memoization(int[] nums) {
        List<Integer> list = new ArrayList<>();
        Map<Integer, Integer> cache = new HashMap<>();
        int maxLength = Integer.MIN_VALUE;
        for (int i = 0; i < nums.length; i++) {
            maxLength = Math.max(maxLength, memoizationDfs(nums, i, cache));
        }
        return maxLength;
    }

    private int memoizationDfs(int[] nums, int i, Map<Integer, Integer> cache) {
        if (cache.containsKey(i)) {
            return cache.get(i);
        }
        int length = 1;
        for (int j = i + 1; j < nums.length; j++) {
            if (nums[i] < nums[j]) {
                length = Math.max(length, 1 + memoizationDfs(nums, j, cache));
            }
        }

        cache.put(i, length);
        return length;
    }

    static void main() {
        LongestIncreasingSubsequence sut = new LongestIncreasingSubsequence();

        int[] nums = {9,1,4,2,3,3,7};
        System.out.println(sut.lengthOfLIS(nums));
        int[] nums2 = {0,3,1,3,2,3};
        System.out.println(sut.lengthOfLIS(nums2));

        System.out.println(sut.memoization(nums));
        System.out.println(sut.memoization(nums2));
    }
}
