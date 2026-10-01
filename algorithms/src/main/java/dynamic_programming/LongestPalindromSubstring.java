package main.java.dynamic_programming;

public class LongestPalindromSubstring {

    //Two pointers
    private String fromMiddle(String s) {
        String result = "";
        int resLength = 0;

        for (int i=0; i<s.length(); i++) {
            //odd length
            int left = i;
            int right = i;
            while (left>=0 && right<s.length() && s.charAt(left)==s.charAt(right)) {
                if ((right - left + 1) > resLength) {
                    result = s.substring(left, right+1);
                    resLength = right - left + 1;
                }
                left--;
                right++;
            }
            //even length
            left = i;
            right = i+1;
            while (left>=0 && right<s.length() && s.charAt(left)==s.charAt(right)) {
                if ((right - left + 1) > resLength) {
                    result = s.substring(left, right+1);
                    resLength = right - left + 1;
                }
                left--;
                right++;
            }
        }
        return result;
    }

    private String memoization(String s) {
        int n = s.length();
        boolean[][] dp = new boolean[n][n];

        int resIndex = 0;
        int resLength = 0;

        for (int left=n-1; left>=0; left--) {
            for (int right=left; right<n; right++) {
                if (s.charAt(left) == s.charAt(right)
                    && (right - left <=2 || dp[left+1][right-1])
                ) {
                    dp[left][right] = true;
                    if (resLength < (right - left + 1)) {
                        resLength = right - left + 1;
                        resIndex = left;
                    }
                }
            }
        }
        return s.substring(resIndex, resIndex + resLength);
    }

    static void main() {
        LongestPalindromSubstring sut = new LongestPalindromSubstring();
        String odd = "ababd";
        String even = "abbc";
        String t = "aaaaa";
        System.out.println(sut.fromMiddle(odd));
        System.out.println(sut.fromMiddle(even));
        System.out.println(sut.fromMiddle(t));

        System.out.println(sut.memoization(odd));
        System.out.println(sut.memoization(even));
        System.out.println(sut.memoization(t));
    }

}
