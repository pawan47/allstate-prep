package com.allstate.prep.demos;

import java.util.*;
import java.util.concurrent.*;

/**
 * ANSWERS THREE REPORTED ALLSTATE QUESTIONS:
 *   "Difference between concurrent hashmap and hashmap?"
 *   "Why to use concurrent hashmap?"
 *   "How hash set works?"  (see the bottom section)
 *
 * Run it:  mvn -q compile exec:java -Dexec.mainClass=com.allstate.prep.demos.HashMapVsConcurrentHashMapDemo
 *
 * This one actually CORRUPTS a HashMap in front of you. That story — "I've
 * watched a plain HashMap silently lose writes under load" — is what separates
 * a real answer from a recited one.
 */
public class HashMapVsConcurrentHashMapDemo {

    private static final int THREADS = 8;
    private static final int PER_THREAD = 20_000;
    private static final int EXPECTED = THREADS * PER_THREAD;

    public static void main(String[] args) throws Exception {
        System.out.println("Hammering each map with " + THREADS + " threads x "
                + PER_THREAD + " puts = " + EXPECTED + " distinct keys expected.\n");

        int plain = hammer(new HashMap<>());
        System.out.println("HashMap            final size = " + plain
                + (plain == EXPECTED ? "  (got lucky this run — rerun it)" : "  <-- LOST " + (EXPECTED - plain) + " WRITES"));

        int sync = hammer(Collections.synchronizedMap(new HashMap<>()));
        System.out.println("synchronizedMap    final size = " + sync + "  (correct, but one global lock)");

        int concurrent = hammer(new ConcurrentHashMap<>());
        System.out.println("ConcurrentHashMap  final size = " + concurrent + "  (correct AND concurrent)");

        System.out.println("""

            ── THE ANSWER TO GIVE ──────────────────────────────────────────
            HashMap is unsynchronised. Concurrent puts race on the same bucket,
            so writes get lost, size() drifts, and pre-Java-8 a concurrent
            resize could even spin forever on a circular list in a bucket.

            Three levels of fix:
              1. Collections.synchronizedMap — every method takes ONE lock on
                 the whole map. Correct, but all threads serialise, and you
                 still need external sync for compound ops (get-then-put).
              2. Hashtable — legacy, synchronised on every method, same
                 bottleneck, does not allow null key or values. Don't use it.
              3. ConcurrentHashMap — Java 8+ locks a single BIN via CAS plus a
                 synchronized block on the bin's head node (pre-8 it was 16
                 lock stripes / Segments). Reads are lock-free volatile reads.
                 Throughput scales with cores.

            Other differences worth stating unprompted:
              - HashMap allows one null key and null values.
                ConcurrentHashMap forbids BOTH — because a null return from
                get() would be ambiguous between "absent" and "mapped to null"
                in a map that can change between your two calls.
              - Iterators: HashMap fail-fast (CME), CHM weakly consistent.
              - CHM gives you atomic compound ops you would otherwise have to
                lock around: putIfAbsent, computeIfAbsent, merge, replace.
              - size() on CHM is an estimate under concurrent writes; use
                mappingCount() (returns long) for large maps.
            ────────────────────────────────────────────────────────────────

            ── "HOW DOES HASHSET WORK?" (reported at Allstate) ─────────────
            HashSet wraps a HashMap. Literally:
                private transient HashMap<E,Object> map;
                private static final Object PRESENT = new Object();
                public boolean add(E e) { return map.put(e, PRESENT) == null; }
            That single line explains everything they'll ask next:
              - uniqueness comes from HashMap key uniqueness (hashCode+equals)
              - add() returns false on a duplicate because put() returned
                non-null for an existing key
              - no ordering, because HashMap has none
              - one null element allowed, because HashMap allows one null key
              - O(1) average, O(log n) worst case since Java 8 treeifies bins
            ────────────────────────────────────────────────────────────────""");
    }

    private static int hammer(Map<Integer, Integer> map) throws Exception {
        ExecutorService pool = Executors.newFixedThreadPool(THREADS);
        CountDownLatch start = new CountDownLatch(1);
        for (int t = 0; t < THREADS; t++) {
            final int base = t * PER_THREAD;
            pool.submit(() -> {
                start.await();
                for (int i = 0; i < PER_THREAD; i++) map.put(base + i, i);
                return null;
            });
        }
        start.countDown();
        pool.shutdown();
        pool.awaitTermination(60, TimeUnit.SECONDS);
        return map.size();
    }
}
