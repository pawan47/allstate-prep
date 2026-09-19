package com.allstate.prep.ds;

/**
 * Build a HashMap from scratch. This is the single highest-leverage exercise
 * for Allstate's L1 round, because "How does HashMap work internally?" and
 * "How does HashSet work?" are the most-reported questions in the bank.
 *
 * Implementing it once means you can answer from memory of the mechanism
 * rather than from a memorised paragraph — that is the difference the
 * interviewers explicitly say they screen for.
 *
 * Must-say facts once you've built it:
 *   - Default capacity 16, load factor 0.75, resize doubles and rehashes
 *   - Java 8+: bucket becomes a red-black tree at TREEIFY_THRESHOLD = 8
 *     (and untreeifies at 6), so worst case is O(log n), not O(n)
 *   - hash spreading: (h = key.hashCode()) ^ (h >>> 16) mixes high bits down
 *     because index = hash & (capacity - 1) only looks at the low bits
 *   - HashSet is literally a HashMap<E, Object> with a shared PRESENT sentinel
 */
public class MyHashMap<K, V> {

    public MyHashMap() {
        throw new UnsupportedOperationException("TDD: write the test first, then build me.");
    }

    /** @return the previous value for this key, or null if none. */
    public V put(K key, V value) {
        throw new UnsupportedOperationException("not implemented");
    }

    public V get(K key) {
        throw new UnsupportedOperationException("not implemented");
    }

    public V remove(K key) {
        throw new UnsupportedOperationException("not implemented");
    }

    public boolean containsKey(K key) {
        throw new UnsupportedOperationException("not implemented");
    }

    public int size() {
        throw new UnsupportedOperationException("not implemented");
    }

    /** Expose this for your own tests: proves resize actually happened. */
    public int capacity() {
        throw new UnsupportedOperationException("not implemented");
    }
}
