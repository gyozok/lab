package main.java.bitmanipulation;

public class CountingBits {

    public int[] countBits(int n) {
        int[] res = new int[n+1];
        for (int i = 0; i<=n; i++) {
            int num = i;
            int count = 0;
            while (num > 0) {
                if ((num & 1) == 1) {
                    count++;
                }
                num = num >> 1;
            }
            res[i] = count;
        }

        return res;
    }

    public int[] countBits2(int n) {
        int[] res = new int[n+1];
        for (int num = 1; num<=n; num++) {
            for (int i=0; i<32; i++) {
                if ((num & (1 << i)) != 0) {
                    res[num]++;
                }
            }
        }

        return res;
    }

    /**
     * 0 - 0000
     * 1 - 0001
     * 2 - 0010
     * 3 - 0011
     * 4 - 0100
     * 5 - 0101
     * 6 - 0110
     * 7 - 0111
     * 8 - 1000
     * the pattern is: 1 extra 1 bit compare to the n - offset,
     * in case of 7 the offset is 4 so 1 more 1 bit compare to 7-4 and 3 hast 1 more bit compare to 3-2, and 1 has 1 extra bit compare to 1-1
     * */
    public int[] dynamicProgramming(int n) {
        int[] dp = new int[n + 1];
        int offset = 1;

        for (int i=1; i<=n; i++) {
            if (offset * 2 == i) {
                offset = i;
            }
            dp[i] = 1 + dp[i-offset];
        }

        return dp;
    }

    static void main() {
        CountingBits sut = new CountingBits();

        System.out.println(sut.countBits(4));

        System.out.println(sut.countBits2(4));

        System.out.println(sut.dynamicProgramming(4));
    }
}
