package com.allstate.prep.strings;

import org.junit.jupiter.api.*;
import static org.assertj.core.api.Assertions.assertThat;

class StringProblemsTest {

    private final StringProblems s = new StringProblems();

    @Nested
    @DisplayName("LC3 longest substring without repeating chars — REPORTED AT ALLSTATE")
    class LongestSubstring {
        @Test void classicCase()      { assertThat(s.lengthOfLongestSubstring("abcabcbb")).isEqualTo(3); }
        @Test void allSameChar()      { assertThat(s.lengthOfLongestSubstring("bbbbb")).isEqualTo(1); }
        @Test void windowMustSlide()  { assertThat(s.lengthOfLongestSubstring("pwwkew")).isEqualTo(3); }
        @Test void emptyString()      { assertThat(s.lengthOfLongestSubstring("")).isZero(); }
        @Test void singleChar()       { assertThat(s.lengthOfLongestSubstring("a")).isEqualTo(1); }
        @Test void noRepeatsAtAll()   { assertThat(s.lengthOfLongestSubstring("abcdef")).isEqualTo(6); }
        @Test void repeatOutsideWindow() { assertThat(s.lengthOfLongestSubstring("abba")).isEqualTo(2); }
    }

    @Nested
    @DisplayName("LC443 string compression — REPORTED AT ALLSTATE")
    class Compression {
        @Test void runsGetCounts()    { assertThat(s.compress("aabbbcc")).isEqualTo("a2b3c2"); }
        @Test void singlesStayBare()  { assertThat(s.compress("abc")).isEqualTo("abc"); }
        @Test void multiDigitCount()  { assertThat(s.compress("aaaaaaaaaaaab")).isEqualTo("a12b"); }
        @Test void emptyStaysEmpty()  { assertThat(s.compress("")).isEqualTo(""); }
        @Test void oneChar()          { assertThat(s.compress("a")).isEqualTo("a"); }
    }

    @Nested
    class ReverseWords {
        @Test void reversesWordOrder() { assertThat(s.reverseWords("the sky is blue")).isEqualTo("blue is sky the"); }
        @Test void trimsAndCollapses() { assertThat(s.reverseWords("  hello   world  ")).isEqualTo("world hello"); }
        @Test void singleWord()        { assertThat(s.reverseWords("java")).isEqualTo("java"); }
    }

    @Nested
    class FirstUnique {
        @Test void findsFirstUnique()  { assertThat(s.firstUniqueChar("leetcode")).isZero(); }
        @Test void skipsRepeats()      { assertThat(s.firstUniqueChar("loveleetcode")).isEqualTo(2); }
        @Test void noneUnique()        { assertThat(s.firstUniqueChar("aabb")).isEqualTo(-1); }
    }
}
