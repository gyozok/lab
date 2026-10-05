package main.java.dynamic_programming;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WordBreak {
    Map<String, Boolean> cache = new HashMap<>();

    public boolean wordBreak(String s, List<String> wordDict) {
        if (s.length() == 0) return true;
        for (String word : wordDict) {
            if (s.startsWith(word)) {
                boolean resp =  wordBreak(s.substring(word.length()), wordDict);
                if (resp) return true;
            }
        }
        return false;
    }

    public boolean memoization(String s, List<String> wordDict) {
        if (s.length() == 0) {
            return true;
        }
        if (cache.containsKey(s)) {
            return cache.get(s);
        }

        for (String word : wordDict) {
            if (s.startsWith(word)) {
                boolean resp = memoization(s.substring(word.length()), wordDict);
                if (resp) {
                    cache.put(s, true);
                    return true;
                }
            }
        }
        cache.put(s, false);
        return false;
    }

    public boolean bottomUp(String s, List<String> wordDict) {
        boolean[] dp = new boolean[s.length()+1];
        dp[s.length()] = true;

        for (int i=s.length() - 1; i>=0; i--) {
            for (String word : wordDict) {
                if (i + word.length()<=s.length() && s.substring(i, i+word.length()).equals(word)) {
                    dp[i] = dp[i + word.length()];
                }
                if (dp[i]) {
                    break;
                }
            }
        }

        return dp[0];
    }

    static void main() {
        WordBreak sut = new WordBreak();
        String s = "applepenapple";
        List<String> wordDict = List.of("apple", "pen", "ape");
        System.out.println(sut.wordBreak(s, wordDict));
        String s2 = "catsincars";
        List<String> wordDict2 = List.of("cats","cat","sin","in","car");
        System.out.println(sut.wordBreak(s2, wordDict2));

        System.out.println(sut.memoization(s, wordDict));
        sut.cache = new HashMap<>();
        System.out.println(sut.memoization(s2, wordDict2));

        System.out.println(sut.bottomUp(s, wordDict));
        System.out.println(sut.bottomUp(s2, wordDict2));
    }
}
