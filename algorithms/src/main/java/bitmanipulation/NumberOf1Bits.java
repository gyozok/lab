package main.java.bitmanipulation;

public class NumberOf1Bits {

    public int hammingWeight(int n) {
        int count = 0;
        while (n>0) {
            if ((n & 1) == 1) {
                count++;
            }
            n = n >> 1;
        }

        return count;
    }

    public int hammingWeight2(int n) {
        int count = 0;
        while (n != 0) {
            n = n & (n-1);
            count++;
        }
        return count;
    }

    public int hammingWeight3(int n) {
        return Integer.bitCount(n);
    }

    static void main() {
        NumberOf1Bits sut = new NumberOf1Bits();

        System.out.println(sut.hammingWeight(23));
        System.out.println(sut.hammingWeight(2147483645));

        System.out.println(sut.hammingWeight2(23));
        System.out.println(sut.hammingWeight2(2147483645));

        System.out.println(sut.hammingWeight3(23));
        System.out.println(sut.hammingWeight3(2147483645));
    }
}
