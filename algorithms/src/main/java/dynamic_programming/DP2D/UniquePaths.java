package main.java.dynamic_programming.DP2D;

public class UniquePaths {

    public int uniquePaths(int m, int n) {

        return bruteForce(0, 0, m, n);
    }

    private int bruteForce(int r, int c, int rows, int cols) {
        if (r == rows || c == cols) {
            return 0;
        }
        if (r == rows-1 && c == cols-1) {
            return 1;
        }
        return bruteForce(r+1, c, rows, cols) +
                bruteForce(r, c+1, rows, cols);
    }

    public int memoization(int r, int c, int m, int n, int[][] cache) {
        if (r == m || c == n) {
            return 0;
        }
        if (cache[r][c] != 0) {
            return cache[r][c];
        }
        if (r == m-1 && c == n-1) {
            return 1;
        }
        cache[r][c] = memoization(r+1, c, m, n, cache) +
                memoization(r, c+1, m, n, cache);
        return cache[r][c];
    }

    public int bottomUp(int rows, int cols) {
        int[] prevRow = new int[cols];
        for (int r = rows-1; r>=0; r--) {
            int[] currRow = new int[cols];
            currRow[cols-1] = 1;
            for (int c=cols-2; c>=0; c--) {
                currRow[c] = currRow[c+1] + prevRow[c];
            }
            prevRow = currRow;
        }
        return prevRow[0];
    }

    static void main() {
        UniquePaths sut = new UniquePaths();

        System.out.println(sut.uniquePaths(3, 6));
        System.out.println(sut.uniquePaths(3, 3));

        int[][] cache = new int[3][6];
        System.out.println(sut.memoization(0,0,3, 6, cache));
        cache = new int[3][3];
        System.out.println(sut.memoization(0,0,3, 3, cache));

        System.out.println(sut.bottomUp(3, 6));
        System.out.println(sut.bottomUp(3, 3));
    }
}
