package main.java.dynamic_programming;

public class PalindromicSubstrings {

    private int twoPointers(String s) {
        int count = 0;
        for (int i = 0; i<s.length(); i++) {
            //odd length substring
            int left = i;
            int right = i;
            while (left>=0 && right <s.length()
            && s.charAt(left) == s.charAt(right)) {
                count++;
                left--;
                right++;
            }

            //even length substring
            left = i;
            right = i + 1;
            while (left>=0 && right<s.length() && s.charAt(left) == s.charAt(right)) {
                count++;
                left--;
                right++;
            }
        }

        return count;
    }

    private int memoization(String s) {
        int n=s.length();
        boolean[][] dp = new boolean[n][n];

        int count = 0;

        for (int left = n; left>=0; left--) {
            for (int right = left; right<n; right++) {
                if (s.charAt(left) == s.charAt(right)
                    && (right -left <= 1 || dp[left+1][right-1])
                ) {
                    dp[left][right] = true;
                    count++;
                }
            }
        }

        return count;
    }

    static void main() {
        PalindromicSubstrings sut = new PalindromicSubstrings();
        String s1 = "abc";
        String s2 = "aaa";

        System.out.println(sut.twoPointers(s1));
        System.out.println(sut.twoPointers(s2));

        System.out.println(sut.memoization(s1));
        System.out.println(sut.memoization(s2));
    }
}
