package com.allstate.prep.ds;

import org.junit.jupiter.api.*;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Build this and "how does HashMap work internally?" stops being a memory test.
 */
@DisplayName("MyHashMap — the mechanism behind Allstate's #1 reported question")
class MyHashMapTest {

    @Test
    void putThenGet() {
        MyHashMap<String, Integer> m = new MyHashMap<>();
        m.put("a", 1);
        assertThat(m.get("a")).isEqualTo(1);
    }

    @Test
    void absentKeyIsNull() {
        assertThat(new MyHashMap<String, Integer>().get("nope")).isNull();
    }

    @Test
    void putReturnsPreviousValueAndOverwrites() {
        MyHashMap<String, Integer> m = new MyHashMap<>();
        m.put("a", 1);
        assertThat(m.put("a", 2)).isEqualTo(1);
        assertThat(m.get("a")).isEqualTo(2);
        assertThat(m.size()).isEqualTo(1);   // overwrite must NOT grow size
    }

    @Test
    void handlesCollidingKeys() {
        // "Aa" and "BB" have IDENTICAL hashCode() in Java — the classic
        // collision demo. Both must survive in the same bucket.
        MyHashMap<String, Integer> m = new MyHashMap<>();
        m.put("Aa", 1);
        m.put("BB", 2);
        assertThat(m.get("Aa")).isEqualTo(1);
        assertThat(m.get("BB")).isEqualTo(2);
        assertThat(m.size()).isEqualTo(2);
    }

    @Test
    void supportsNullKeyLikeRealHashMap() {
        MyHashMap<String, Integer> m = new MyHashMap<>();
        m.put(null, 7);
        assertThat(m.get(null)).isEqualTo(7);
    }

    @Test
    void removeReturnsOldValueAndShrinks() {
        MyHashMap<String, Integer> m = new MyHashMap<>();
        m.put("a", 1);
        assertThat(m.remove("a")).isEqualTo(1);
        assertThat(m.get("a")).isNull();
        assertThat(m.size()).isZero();
        assertThat(m.remove("a")).isNull();
    }

    @Test
    void containsKeyDistinguishesNullValueFromAbsent() {
        MyHashMap<String, Integer> m = new MyHashMap<>();
        m.put("a", null);
        assertThat(m.containsKey("a")).isTrue();
        assertThat(m.containsKey("b")).isFalse();
    }

    @Test
    void resizesPastTheLoadFactorAndKeepsEveryEntry() {
        MyHashMap<Integer, Integer> m = new MyHashMap<>();
        for (int i = 0; i < 100; i++) m.put(i, i * 10);

        assertThat(m.size()).isEqualTo(100);
        assertThat(m.capacity()).isGreaterThan(16);   // default cap was exceeded
        for (int i = 0; i < 100; i++) {
            assertThat(m.get(i)).as("key %d survived rehash", i).isEqualTo(i * 10);
        }
    }
}
