package com.allstate.prep.strings;

/**
 * Allstate leans on ARRAY and STRING problems more than graphs/DP.
 * Everything here is from the reported Allstate question bank.
 */
public class StringProblems {

    /**
     * REPORTED IN THE TECHNICAL SCREEN, by name.
     * "Longest substring without repeating characters" (LeetCode 3, Medium).
     *
     * Brute force is O(n^3). Get to the sliding window with a last-seen index
     * map: O(n) time, O(min(n, charset)) space. State the complexity BEFORE
     * you write code — they probe time complexity explicitly.
     *
     * "abcabcbb" -> 3   "bbbbb" -> 1   "pwwkew" -> 3   "" -> 0
     */
    public int lengthOfLongestSubstring(String s) {
        throw new UnsupportedOperationException("not implemented");
    }

    /**
     * REPORTED: "String Compression" (LeetCode 443, Medium).
     * Run-length encode in place-ish: aabbbcc -> a2b3c2.
     * Careful with the contract: a single char stays bare (a1 is wrong -> "a"),
     * and counts >= 10 span multiple characters.
     *
     * "aabbbcc" -> "a2b3c2"   "abc" -> "abc"   "aaaaaaaaaaaab" -> "a12b"
     */
    public String compress(String input) {
        throw new UnsupportedOperationException("not implemented");
    }

    /**
     * Classic warm-up they use to check you handle Unicode and nulls sanely.
     * Reverse the words, not the characters: "the sky is blue" -> "blue is sky the".
     * Collapse extra whitespace. Do it without String.split() if they ask.
     */
    public String reverseWords(String s) {
        throw new UnsupportedOperationException("not implemented");
    }

    /**
     * First non-repeating character's index, or -1.
     * Good place to show you know LinkedHashMap preserves insertion order,
     * and that a 26-slot int[] beats a HashMap for pure-ASCII input.
     */
    public int firstUniqueChar(String s) {
        throw new UnsupportedOperationException("not implemented");
    }
}
