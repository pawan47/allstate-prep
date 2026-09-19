package com.allstate.prep.ds;

/**
 * ALLSTATE'S SIGNATURE TDD PAIR-PROGRAMMING PROBLEM.
 *
 * Reported verbatim from candidate experiences: "implement the functionality of
 * Set without using any collections" while the interviewer drives TDD.
 *
 * RULES OF THE DRILL:
 *   - No java.util.* anything. Raw arrays only.
 *   - Write the failing test FIRST, then the minimum code to pass it.
 *   - Narrate every decision out loud. Silence loses this round, not wrong code.
 *
 * Talking points the interviewer is fishing for:
 *   - hashCode()/equals() contract, and why you bucket by hash
 *   - collision strategy (chaining vs open addressing) and WHY you chose yours
 *   - load factor + resize, and the amortised O(1) claim
 *   - null handling, and the "add returns false on duplicate" contract
 */
public class MySet<E> {

    // Hint: Object[] buckets, each bucket a small linked Node chain you define
    //       yourself. Track size and threshold for resize.

    public MySet() {
        throw new UnsupportedOperationException("TDD: write the test first, then build me.");
    }

    /** @return true if this set did NOT already contain the element. */
    public boolean add(E element) {
        throw new UnsupportedOperationException("not implemented");
    }

    public boolean contains(E element) {
        throw new UnsupportedOperationException("not implemented");
    }

    /** @return true if the element was present and removed. */
    public boolean remove(E element) {
        throw new UnsupportedOperationException("not implemented");
    }

    public int size() {
        throw new UnsupportedOperationException("not implemented");
    }

    public boolean isEmpty() {
        throw new UnsupportedOperationException("not implemented");
    }
}
