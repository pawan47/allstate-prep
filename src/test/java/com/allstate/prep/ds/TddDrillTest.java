package com.allstate.prep.ds;

import org.junit.jupiter.api.*;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * ✍️ THIS FILE IS YOURS. It is empty on purpose.
 *
 * This is the actual Allstate round: the interviewer gives a requirement,
 * YOU write the test, then you make it pass — out loud, as the driver.
 *
 * Run just this file on a loop while you work:
 *     mvn -q test -Dtest=TddDrillTest
 *
 * Drill discipline — one cycle at a time, never skip ahead:
 *     RED    write one failing test. Run it. SEE it fail for the right reason.
 *     GREEN  simplest code that passes. Hardcoding is legal in this step.
 *     REFACTOR  remove the hardcoding, keep it green.
 *
 * Say these out loud as you go; they are what's actually being scored:
 *   - "I'll start with the simplest behaviour: an empty set has size zero."
 *   - "This test fails because I haven't written the field yet — good."
 *   - "I'm hardcoding true here to get green, then I'll generalise."
 *   - "Next edge case I'm worried about is hash collisions."
 */
class TddDrillTest {

    @Test
    @Disabled("delete this annotation and write your first real test")
    void myFirstFailingTest() {
        // Start here. Example of a proper first cycle:
        //   MySet<String> s = new MySet<>();
        //   assertThat(s.size()).isZero();
        fail("write me");
    }

    private static void fail(String msg) {
        throw new AssertionError(msg);
    }
}
