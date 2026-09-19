package com.allstate.prep.demos;

import java.util.*;
import java.util.concurrent.*;

/**
 * ANSWERS TWO REPORTED ALLSTATE QUESTIONS AT ONCE:
 *   "What is fail-safe and fail-fast?"
 *   "What happens when we iterate over concurrent hashmap and hashmap?"
 *
 * Run it:  mvn -q compile exec:java -Dexec.mainClass=com.allstate.prep.demos.FailFastVsFailSafeDemo
 *
 * Seeing the exception land is worth more than reading the definition, because
 * the follow-up is always "have you actually hit this?" — now you have.
 */
public class FailFastVsFailSafeDemo {

    public static void main(String[] args) {
        System.out.println("=== 1. ArrayList iterator: FAIL-FAST ===");
        List<String> list = new ArrayList<>(List.of("a", "b", "c"));
        try {
            for (String s : list) {          // sugar for list.iterator()
                System.out.println("  visiting " + s);
                if (s.equals("a")) list.add("d");   // structural modification
            }
        } catch (ConcurrentModificationException e) {
            System.out.println("  -> ConcurrentModificationException thrown.");
            System.out.println("     WHY: the iterator captured modCount at creation. Every");
            System.out.println("     next() re-checks it. list.add bumped modCount, so the");
            System.out.println("     iterator knows its view is stale and bails immediately.");
        }

        System.out.println("\n=== 2. HashMap keySet iterator: also FAIL-FAST ===");
        Map<String, Integer> hashMap = new HashMap<>(Map.of("a", 1, "b", 2));
        try {
            for (String k : hashMap.keySet()) {
                System.out.println("  visiting " + k);
                hashMap.put("c", 3);
            }
        } catch (ConcurrentModificationException e) {
            System.out.println("  -> ConcurrentModificationException. Same modCount mechanism.");
            System.out.println("     NOTE: it is best-effort, NOT a guarantee. Never write code");
            System.out.println("     that depends on it firing — it is a bug detector only.");
        }

        System.out.println("\n=== 3. The legal way to remove while iterating a HashMap ===");
        Map<String, Integer> removable = new HashMap<>();
        removable.put("keep", 1);
        removable.put("drop", 2);
        Iterator<Map.Entry<String, Integer>> it = removable.entrySet().iterator();
        while (it.hasNext()) {
            if (it.next().getKey().equals("drop")) it.remove();  // iterator.remove syncs modCount
        }
        System.out.println("  after iterator.remove(): " + removable);
        System.out.println("  (or removeIf(...) — same effect, one line)");

        System.out.println("\n=== 4. ConcurrentHashMap: FAIL-SAFE / weakly consistent ===");
        Map<String, Integer> chm = new ConcurrentHashMap<>();
        chm.put("a", 1);
        chm.put("b", 2);
        for (String k : chm.keySet()) {
            System.out.println("  visiting " + k);
            chm.put("added-during-iteration", 99);   // no exception
        }
        System.out.println("  -> No exception. Final map: " + chm);
        System.out.println("     WHY: its iterator is WEAKLY CONSISTENT — it reads live buckets,");
        System.out.println("     tolerates concurrent writes, and may or may not reflect entries");
        System.out.println("     added after it started. Never throws CME.");

        System.out.println("\n=== 5. CopyOnWriteArrayList: FAIL-SAFE by snapshot ===");
        List<String> cow = new CopyOnWriteArrayList<>(List.of("a", "b"));
        for (String s : cow) {
            System.out.println("  visiting " + s);
            cow.add("c");        // writes to a fresh copy of the backing array
        }
        System.out.println("  -> No exception. Final list: " + cow);
        System.out.println("     The iterator walks an immutable SNAPSHOT of the 2 original elements,");
        System.out.println("     so it never sees the adds — it ran exactly twice, which is why");
        System.out.println("     two copies of the array were made and two cs landed.");
        System.out.println("     Cost: every write copies the whole array. Read-heavy use only.");

        System.out.println("""

            ── THE ANSWER TO GIVE ──────────────────────────────────────────
            Fail-fast iterators (ArrayList, HashMap, LinkedList) track a
            modCount and throw ConcurrentModificationException the moment they
            detect the collection changed underneath them. It is a best-effort
            bug detector, not a synchronisation guarantee.

            Fail-safe iterators never throw, by not iterating the live data:
              - CopyOnWriteArrayList iterates an immutable snapshot
              - ConcurrentHashMap is weakly consistent — live but tolerant
            The trade-off is staleness and, for COW, copy cost per write.
            ────────────────────────────────────────────────────────────────""");
    }
}
