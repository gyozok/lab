package main.java.bitmanipulation;

public class ReverseBits {

    public int reverseBits(int n) {
        int result = 0;

        for (int i=0; i<32; i++) {
            int leftBit = n & 1;
            result = (result << 1) + leftBit;
            n = n>>1;
        }
        return result;
    }

    public int reverseBits2(int n) {
        int res = 0;
        for (int i = 0; i < 32; i++) {
            int bit = (n >> i) & 1;
            res += (bit << (31 - i));
        }
        return res;
    }

    static void main() {
        ReverseBits sut = new ReverseBits();

        System.out.println(sut.reverseBits(21));
        System.out.println(sut.reverseBits2(0b00000000000000000000000000010101));
    }
}
