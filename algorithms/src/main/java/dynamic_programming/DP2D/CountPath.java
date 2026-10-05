package main.java.dynamic_programming.DP2D;

public class CountPath {

    public int bruteforce(int r, int c, int rows, int cols) {
        if (r==rows || c == cols) {
            return 0;
        }
        if (r == rows - 1 && c == cols - 1) {
            return 1;
        }

        return (bruteforce(r+1, c, rows, cols) + //move down
                bruteforce(r, c+1, rows, cols)); //move right
    }

    public int memoization(int r, int c, int rows, int cols, int[][] cache) {
        if (r == rows || c == cols) {
            return 0;
        }
        if (cache[r][c] != 0) {
            return cache[r][c];
        }

        if (r==rows-1 && c == cols-1) {
            return 1;
        }

        cache[r][c] = (memoization(r+1, c, rows, cols, cache) + //move down
                memoization(r, c+1, rows, cols, cache)); //move right

        return cache[r][c];
    }

    public int bottomUp(int rows, int cols) {
        int[] prevRow = new int[cols];

        for (int r = rows - 1; r>=0; r--) {
            int[] curRow = new int[cols];
            curRow[cols - 1] = 1;
            for (int c = cols-2; c>=0; c--) {
                curRow[c] = curRow[c+1] + prevRow[c];
            }
            prevRow = curRow;
        }
        return prevRow[0];
    }

    static void main() {
        CountPath sut = new CountPath();

        System.out.println(sut.bruteforce(0, 0, 4, 4));

        int[][] cache = new int[4][4];
        System.out.println(sut.memoization(0, 0, 4, 4, cache));

        System.out.println(sut.bottomUp(4, 4));
    }
}
