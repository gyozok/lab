package main.java.dynamic_programming.DP2D;

public class LongestCommonSubsequence {

    public int longestCommonSubsequence(String text1, String text2) {
        return dfs(text1, 0, text2, 0);
    }

    private int dfs(String text1, int i1, String text2, int i2) {
        if (i1 == text1.length() || i2 == text2.length()) {
            return 0;
        }
        if (text1.charAt(i1) == text2.charAt(i2)) {
            return dfs(text1, i1+1, text2, i2+1) + 1;
        }
        return Math.max(dfs(text1, i1+1, text2, i2), dfs(text1, i1, text2, i2+1));
    }

    private int memoization(String text1, String text2) {
        int[][] cache = new int[text1.length()][text2.length()];
        for (int i1 = 0; i1<text1.length(); i1++) {
            for (int i2 = 0; i2<text2.length(); i2++) {
                cache[i1][i2] = -1;
            }
        }
        return memoizationDfs(text1, 0, text2, 0, cache);
    }

    private int memoizationDfs(String text1, int i1, String text2, int i2, int[][] cache) {
        if (i1 == text1.length() || i2 == text2.length()) {
            return 0;
        }
        int val;
        if ((val = cache[i1][i2]) != -1) {
            return val;
        }

        if (text1.charAt(i1) == text2.charAt(i2)) {
            cache[i1][i2] = memoizationDfs(text1, i1+1, text2, i2+1, cache) + 1;
        } else {
            cache[i1][i2] = Math.max(memoizationDfs(text1, i1+1, text2, i2+1, cache),
                    memoizationDfs(text1, i1, text2, i2+1, cache));
        }
        return cache[i1][i2];
    }

    public int bottomUp(String text1, String text2) {
        int[][] cache = new int[text1.length() + 1][text2.length() + 1];

        for (int i1 = text1.length() - 1; i1>=0; i1--) {
            for (int i2 = text2.length() - 1; i2>=0; i2--) {
                if (text1.charAt(i1) == text2.charAt(i2)) {
                    cache[i1][i2] = cache[i1+1][i2+1] + 1;
                } else {
                    cache[i1][i2] = Math.max(cache[i1+1][i2], cache[i1][i2+1]);
                }
            }
        }

        return cache[0][0];
    }

    static void main() {
        LongestCommonSubsequence sut = new LongestCommonSubsequence();

        String text1 = "cat";
        String text2 = "crabt";
        System.out.println(sut.longestCommonSubsequence(text1, text2));

        text1 = "abcd";
        text2 = "abcd";
        System.out.println(sut.longestCommonSubsequence(text1, text2));

        text1 = "abcd";
        text2 = "efgh";
        System.out.println(sut.longestCommonSubsequence(text1, text2));

        text1 = "cat";
        text2 = "crabt";
        System.out.println(sut.memoization(text1, text2));

        text1 = "abcd";
        text2 = "abcd";
        System.out.println(sut.memoization(text1, text2));

        text1 = "abcd";
        text2 = "efgh";
        System.out.println(sut.memoization(text1, text2));

        text1 = "cat";
        text2 = "crabt";
        System.out.println(sut.bottomUp(text1, text2));

        text1 = "abcd";
        text2 = "abcd";
        System.out.println(sut.bottomUp(text1, text2));

        text1 = "abcd";
        text2 = "efgh";
        System.out.println(sut.bottomUp(text1, text2));
    }
}
