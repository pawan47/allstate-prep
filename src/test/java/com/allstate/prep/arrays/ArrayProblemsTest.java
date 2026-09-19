package com.allstate.prep.arrays;

import org.junit.jupiter.api.*;
import static org.assertj.core.api.Assertions.assertThat;

class ArrayProblemsTest {

    private final ArrayProblems a = new ArrayProblems();

    @Nested
    @DisplayName("LC33 rotated array search — REPORTED VERBATIM AT ALLSTATE")
    class RotatedSearch {
        @Test void findsInRightHalf()  { assertThat(a.searchRotated(new int[]{4,5,6,7,0,1,2}, 0)).isEqualTo(4); }
        @Test void findsInLeftHalf()   { assertThat(a.searchRotated(new int[]{4,5,6,7,0,1,2}, 6)).isEqualTo(2); }
        @Test void theirExample()      { assertThat(a.searchRotated(new int[]{4,5,6,1,2,3}, 3)).isEqualTo(5); }
        @Test void absentTarget()      { assertThat(a.searchRotated(new int[]{4,5,6,7,0,1,2}, 3)).isEqualTo(-1); }
        @Test void notRotated()        { assertThat(a.searchRotated(new int[]{1,2,3,4,5}, 4)).isEqualTo(3); }
        @Test void singleElementHit()  { assertThat(a.searchRotated(new int[]{1}, 1)).isZero(); }
        @Test void singleElementMiss() { assertThat(a.searchRotated(new int[]{1}, 2)).isEqualTo(-1); }
        @Test void emptyArray()        { assertThat(a.searchRotated(new int[]{}, 1)).isEqualTo(-1); }
    }

    @Nested
    @DisplayName("Pivot of a rotated array — REPORTED VERBATIM AT ALLSTATE")
    class Pivot {
        @Test void findsRotationPoint() { assertThat(a.findPivot(new int[]{4,5,6,7,0,1,2})).isEqualTo(4); }
        @Test void alreadySorted()      { assertThat(a.findPivot(new int[]{1,2,3,4,5})).isZero(); }
        @Test void rotatedByOne()       { assertThat(a.findPivot(new int[]{5,1,2,3,4})).isEqualTo(1); }
        @Test void twoElements()        { assertThat(a.findPivot(new int[]{2,1})).isEqualTo(1); }
    }

    @Nested
    @DisplayName("LC56 merge intervals — REPORTED AT ALLSTATE")
    class MergeIntervals {
        @Test void mergesOverlapping() {
            assertThat(a.mergeIntervals(new int[][]{{1,3},{2,6},{8,10},{15,18}}))
                .isDeepEqualTo(new int[][]{{1,6},{8,10},{15,18}});
        }
        @Test void mergesTouching() {
            assertThat(a.mergeIntervals(new int[][]{{1,4},{4,5}}))
                .isDeepEqualTo(new int[][]{{1,5}});
        }
        @Test void handlesUnsortedInput() {
            assertThat(a.mergeIntervals(new int[][]{{8,10},{1,3},{2,6}}))
                .isDeepEqualTo(new int[][]{{1,6},{8,10}});
        }
        @Test void fullyContainedInterval() {
            assertThat(a.mergeIntervals(new int[][]{{1,10},{2,3}}))
                .isDeepEqualTo(new int[][]{{1,10}});
        }
        @Test void emptyInput() {
            assertThat(a.mergeIntervals(new int[][]{})).isDeepEqualTo(new int[][]{});
        }
    }

    @Nested
    @DisplayName("LC325 max size subarray sum == k — REPORTED AT ALLSTATE")
    class MaxSubArrayLen {
        @Test void theirExample()     { assertThat(a.maxSubArrayLen(new int[]{1,-1,5,-2,3}, 3)).isEqualTo(4); }
        @Test void prefixFromStart()  { assertThat(a.maxSubArrayLen(new int[]{-2,-1,2,1}, 1)).isEqualTo(2); }
        @Test void noSuchSubarray()   { assertThat(a.maxSubArrayLen(new int[]{1,2,3}, 100)).isZero(); }
        @Test void wholeArrayMatches(){ assertThat(a.maxSubArrayLen(new int[]{1,2,3}, 6)).isEqualTo(3); }
        @Test void zerosCountToward() { assertThat(a.maxSubArrayLen(new int[]{0,0,0}, 0)).isEqualTo(3); }
    }

    @Nested
    class TwoSum {
        @Test void findsThePair() { assertThat(a.twoSum(new int[]{2,7,11,15}, 9)).containsExactly(0,1); }
        @Test void laterPair()    { assertThat(a.twoSum(new int[]{3,2,4}, 6)).containsExactly(1,2); }
    }

    @Nested
    class Rotate {
        @Test void rotatesRightByK() {
            int[] nums = {1,2,3,4,5,6,7};
            a.rotate(nums, 3);
            assertThat(nums).containsExactly(5,6,7,1,2,3,4);
        }
        @Test void kLargerThanLength() {
            int[] nums = {1,2};
            a.rotate(nums, 5);
            assertThat(nums).containsExactly(2,1);
        }
    }
}
