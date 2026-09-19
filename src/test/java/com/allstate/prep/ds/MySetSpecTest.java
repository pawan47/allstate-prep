package com.allstate.prep.ds;

import org.junit.jupiter.api.*;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * ⚠️ READ THIS BEFORE YOU LOOK AT THE TESTS BELOW.
 *
 * In the real Allstate round, YOU write the tests while the interviewer drives.
 * So do the drill in this order:
 *
 *   1. Open TddDrillTest.java (it's empty on purpose) and write your own
 *      failing test for MySet.add(). Run it. Watch it go red.
 *   2. Write the minimum code in MySet to make it green.
 *   3. Repeat: contains, duplicates, remove, size, null, resize, collisions.
 *   4. ONLY THEN run this file as a spec check:
 *         mvn -q test -Dtest=MySetSpecTest
 *      Any test here you didn't think of yourself is a gap worth noting.
 *
 * Constraint that makes this the real exercise: NO java.util.* in MySet.
 */
@DisplayName("MySet spec — Allstate's signature TDD pair-programming problem")
class MySetSpecTest {

    @Test
    void startsEmpty() {
        MySet<String> s = new MySet<>();
        assertThat(s.size()).isZero();
        assertThat(s.isEmpty()).isTrue();
    }

    @Test
    void addReturnsTrueForNewElement() {
        assertThat(new MySet<String>().add("a")).isTrue();
    }

    @Test
    void addReturnsFalseForDuplicateAndDoesNotGrow() {
        MySet<String> s = new MySet<>();
        s.add("a");
        assertThat(s.add("a")).isFalse();
        assertThat(s.size()).isEqualTo(1);
    }

    @Test
    void containsFindsWhatWasAdded() {
        MySet<String> s = new MySet<>();
        s.add("a");
        assertThat(s.contains("a")).isTrue();
        assertThat(s.contains("b")).isFalse();
    }

    @Test
    void usesEqualsNotReferenceIdentity() {
        MySet<String> s = new MySet<>();
        s.add(new String("hello"));
        assertThat(s.contains(new String("hello")))
            .as("must compare with equals(), not ==")
            .isTrue();
    }

    @Test
    void survivesHashCollisions() {
        // "Aa" and "BB" share a hashCode in Java.
        MySet<String> s = new MySet<>();
        s.add("Aa");
        s.add("BB");
        assertThat(s.contains("Aa")).isTrue();
        assertThat(s.contains("BB")).isTrue();
        assertThat(s.size()).isEqualTo(2);
    }

    @Test
    void removeReportsWhetherItWasPresent() {
        MySet<String> s = new MySet<>();
        s.add("a");
        assertThat(s.remove("a")).isTrue();
        assertThat(s.contains("a")).isFalse();
        assertThat(s.size()).isZero();
        assertThat(s.remove("a")).isFalse();
    }

    @Test
    void removingTheMiddleOfACollisionChainKeepsTheRest() {
        MySet<String> s = new MySet<>();
        s.add("Aa");
        s.add("BB");
        s.remove("Aa");
        assertThat(s.contains("BB")).as("chain must not be broken").isTrue();
        assertThat(s.size()).isEqualTo(1);
    }

    @Test
    void handlesNullElement() {
        MySet<String> s = new MySet<>();
        assertThat(s.add(null)).isTrue();
        assertThat(s.contains(null)).isTrue();
        assertThat(s.add(null)).isFalse();
        assertThat(s.size()).isEqualTo(1);
    }

    @Test
    void growsBeyondInitialCapacityWithoutLosingElements() {
        MySet<Integer> s = new MySet<>();
        for (int i = 0; i < 1000; i++) s.add(i);
        assertThat(s.size()).isEqualTo(1000);
        for (int i = 0; i < 1000; i++) {
            assertThat(s.contains(i)).as("element %d survived resize", i).isTrue();
        }
        assertThat(s.contains(1000)).isFalse();
    }
}
