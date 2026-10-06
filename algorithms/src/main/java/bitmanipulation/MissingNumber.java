package main.java.bitmanipulation;

public class MissingNumber {

    public int missingNumber(int[] nums) {
        int result = 0;
        for (int i=0; i<=nums.length; i++) {
            result += i;
            if (i<nums.length) {
                result -= nums[i];
            }
        }

        return result;
    }

    public int bitManipulation(int[] nums) {
        int n = nums.length;
        int xor = n;
        for (int i=0; i<n; i++) {
            xor = xor ^ i;
            xor = xor ^ nums[i];
        }

        return xor;
    }

    static void main() {
        MissingNumber sut = new MissingNumber();

        int[] nums = {1,2,3};
        System.out.println(sut.missingNumber(nums));

        int[] nums2 = {0,2};
        System.out.println(sut.missingNumber(nums2));

        int[] nums3 = {0,1,2,3,4,6,7,8,9};
        System.out.println(sut.missingNumber(nums3));

        System.out.println(sut.bitManipulation(nums));
        System.out.println(sut.bitManipulation(nums2));
        System.out.println(sut.bitManipulation(nums3));
    }
}
