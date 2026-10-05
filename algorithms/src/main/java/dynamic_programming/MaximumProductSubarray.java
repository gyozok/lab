package main.java.dynamic_programming;

public class MaximumProductSubarray {

    //Dynamic programming solution
    private int maxProduct(int[] nums) {
        int result = nums[0];
        int minProd = 1;
        int maxProd = 1;
        for (int num : nums) {
             if (num == 0) {
                 minProd = 1;
                 maxProd = 1;
                 result = Math.max(result, 0);
                 continue;
             }

             int tmp = num * maxProd;
             maxProd = Math.max(tmp, num * minProd);
             maxProd = Math.max(maxProd, num);
             minProd = Math.min(tmp, num * minProd);
             minProd = Math.min(minProd, num);
             result = Math.max(result, maxProd);
        }

        return result;
    }

    private int prefixSuffix(int[] nums) {
        int n = nums.length;
        int result = nums[0];
        int prefix = 0;
        int sufix = 0;

        for (int i=0; i<n; i++) {
            prefix = nums[i] * (prefix == 0 ? 1 : prefix);
            sufix = nums[n-i-1] * (sufix == 0 ? 1 : sufix);
            result = Math.max(result, Math.max(prefix, sufix));
        }

        return result;
    }

    static void main() {
        MaximumProductSubarray sut = new MaximumProductSubarray();

        int[] nums = {2,4,-3,5};
        System.out.println(sut.maxProduct(nums));
        int[] nums2 = {-3,0,-2};
        System.out.println(sut.maxProduct(nums2));

        System.out.println(sut.prefixSuffix(nums));
        System.out.println(sut.prefixSuffix(nums2));
    }
}
