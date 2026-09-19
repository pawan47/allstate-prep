package com.allstate.prep.ds;

/**
 * LRU Cache — explicitly reported in Allstate's technical screen
 * ("LRU Cache design/code problems").
 *
 * Target: get and put BOTH in O(1). That means HashMap + doubly linked list.
 * Do NOT reach for LinkedHashMap first — they want to see the mechanism.
 * Mention LinkedHashMap(accessOrder=true) + removeEldestEntry AFTER you've
 * built it by hand; it lands as breadth, not as a shortcut.
 *
 * Follow-up they like: "now make it thread-safe" — talk about the difference
 * between wrapping in synchronized (correct, contended) vs a striped/segmented
 * design, and why ConcurrentHashMap alone does NOT give you LRU eviction.
 */
public class LRUCache {

    public LRUCache(int capacity) {
        throw new UnsupportedOperationException("TDD: write the test first, then build me.");
    }

    /** @return the value, or -1 if absent. Counts as a use. */
    public int get(int key) {
        throw new UnsupportedOperationException("not implemented");
    }

    /** Insert or update. Evicts the least-recently-used entry when full. */
    public void put(int key, int value) {
        throw new UnsupportedOperationException("not implemented");
    }
}
