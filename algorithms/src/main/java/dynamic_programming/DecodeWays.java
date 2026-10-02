package main.java.dynamic_programming;

import java.util.HashMap;
import java.util.Map;

public class DecodeWays {

    private int numDecoding(String s) {
        int count = 0;
        if (s.length()>0) {
            for (int i = 0; i < s.length() && i < 2; i++) {
                int num = Integer.parseInt(s.substring(0, i+1));
                if (!(s.startsWith("0") || (num<=0 || num>26))) {
//                    return 0;
//                }
                    count += numDecoding(s.substring(i + 1));
                }
            }
        } else {
            count++;
        }
        return count;
    }

    private int memoized(String s, Map<String, Integer> cache) {
        int count = 0;
        if (s.length()>0) {
            for (int i=0; i<s.length() && i<2; i++) {
                int num = Integer.parseInt(s.substring(0, i+1));
                if (!s.startsWith("0") && (1<=num && num<=26)) {
                    String sub = s.substring(i+1);
                    if (cache.containsKey(sub)) {
                        count += cache.get(sub);
                    } else {
                        int c = memoized(sub, cache);
                        cache.put(sub, c);
                        count += c;
                    }
                }
            }
        } else {
            count++;
        }
        return count;
    }

    private int memoization2(String s) {
        Map<Integer, Integer> cache = new HashMap<>();
        cache.put(s.length(), 1);
        return memoization2Helper(s, 0, cache);
    }

    private int memoization2Helper(String s, int i, Map<Integer, Integer> dp) {
        if (dp.containsKey(i)) {
            return dp.get(i);
        }
        if (s.charAt(i) == '0') {
            return 0;
        }

        int count = memoization2Helper(s, i+1, dp);
        if (i+1<s.length() && (s.charAt(i) == '1' || s.charAt(i)== '2' && s.charAt(i+1) < '7')) {
            count += memoization2Helper(s, i+2, dp);
        }
        dp.put(i, count);
        return count;
    }

    private int bottomUp(String s) {
        int dp[] = new int[s.length() + 1];
        dp[s.length()] = 1;

        for (int i = s.length()-1; i>=0; i--) {
            if (s.charAt(i) == '0') {
                dp[i] = 0;
            } else {
                dp[i] = dp[i+1];
                if (i + 1 <s.length() && (s.charAt(i) == '1' || s.charAt(i) == '2' && s.charAt(i+1)<'7')) {
                    dp[i] += dp[i+2];
                }
            }
        }

        return dp[0];
    }

    private int bottomUpOptimized(String s) {
        int dp = 0;
        int dp2 = 0;
        int dp1 = 1;

        for (int i=s.length()-1; i>=0; i--) {
            if (s.charAt(i) == '0') {
                dp = 0;
            } else {
                dp = dp1;
                if (i + 1 < s.length() && (s.charAt(i) == '1' || (s.charAt(i) == '2' && s.charAt(i+1) < '7'))) {
                    dp += dp2;
                }
            }
            dp2 = dp1;
            dp1 = dp;
            dp = 0;
        }

        return dp1;
    }

    static void main() {
        DecodeWays sut = new DecodeWays();

        System.out.println(sut.numDecoding("12"));
        System.out.println(sut.numDecoding("01"));
        System.out.println(sut.numDecoding("27"));

        Map<String, Integer> cache = new HashMap<>();
        System.out.println(sut.memoized("12", cache));
        cache = new HashMap<>();
        System.out.println(sut.memoized("01", cache));
        cache = new HashMap<>();
        System.out.println(sut.memoized("27", cache));

        System.out.println(sut.memoization2("12"));
        System.out.println(sut.memoization2("01"));
        System.out.println(sut.memoization2("27"));

        System.out.println(sut.bottomUp("12"));
        System.out.println(sut.bottomUp("01"));
        System.out.println(sut.bottomUp("27"));

        System.out.println(sut.bottomUpOptimized("12"));
        System.out.println(sut.bottomUpOptimized("01"));
        System.out.println(sut.bottomUpOptimized("27"));

    }
}
