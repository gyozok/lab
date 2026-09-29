package main.java.dynamic_programming;

import java.util.HashMap;
import java.util.Map;

public class HouseRobber {

    private int recursive(int i, int[] nums) {
        int n = nums.length;
        if (i >= n-1) {
            return i == n-1 ? nums[i] : 0;
        }

        return nums[i] + Math.max(recursive(i+2, nums), recursive(i+3, nums));
    }

    private int memoization(int i, int[] nums, Map<Integer, Integer> cache) {
        int n = nums.length;
        if (i >= n-1) {
            return i == n-1 ? nums[i] : 0;
        }
        if (cache.containsKey(i)) {
            return cache.get(i);
        }
        int result = nums[i] + Math.max(memoization(i+2, nums, cache), memoization(i+3, nums, cache));
        cache.put(i, result);
        return result;
    }

    private int bottomUp(int[] nums) {
        if (nums.length == 0) return 0;
        if (nums.length == 1) return nums[0];

        int[] cache = new int[nums.length];
        cache[0] = nums[0];
        cache[1] = Math.max(nums[0], nums[1]);

        for (int i=2; i<nums.length; i++) {
            cache[i] = Math.max(
                    cache[i-1], //we dont rob the i th house
                    nums[i] + cache[i-2] //we rob the ith house
            );
        }
        return cache[nums.length - 1];
    }

    private int bottomUpOptimized(int[] nums) {
        if (nums.length == 0) return 0;
        if (nums.length == 1) return nums[0];

        int first = nums[0];
        int second = Math.max(nums[0], nums[1]);

        for (int i=2; i<nums.length; i++) {
            int tmp = Math.max(second, first + nums[i]);
            first = second;
            second = tmp;
        }
        return second;
    }

    static void main() {
        HouseRobber sut = new HouseRobber();

        int[] nums = {2, 9, 8, 3, 6};
        System.out.println(sut.recursive(0, nums));

        Map<Integer, Integer> cache = new HashMap<>();
        System.out.println(Math.max(sut.memoization(0, nums, cache), sut.memoization(1, nums, cache)));

        System.out.println(sut.bottomUp(nums));
        System.out.println(sut.bottomUpOptimized(nums));
    }
}
