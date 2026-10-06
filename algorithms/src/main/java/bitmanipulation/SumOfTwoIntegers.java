package main.java.bitmanipulation;

public class SumOfTwoIntegers {

    public int getSum(int a, int b) {
        int carry = 0;
        int result = 0;

        for (int i=0; i<32; i++) {
            int lastA = (a >> i) & 1;
            int lastB = (b >> i) & 1;
            int currBit = lastA ^ lastB ^ carry;
//            carry= (lastA + lastB + carry) >= 2 ? 1 : 0;
            carry = (lastA & lastB) | (lastA & carry) | (lastB & carry);
            if (currBit != 0) {
                result = result | (1<<i);
            }
        }

        return result;
    }

    static void main() {
        SumOfTwoIntegers sut = new SumOfTwoIntegers();

        System.out.println(sut.getSum(1, 1));
        System.out.println(sut.getSum(4, 7));
    }
}
