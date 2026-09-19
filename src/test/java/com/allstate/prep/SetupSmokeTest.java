package com.allstate.prep;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

/** Run this first: `mvn -q test -Dtest=SetupSmokeTest`. If it's green, the dojo works. */
class SetupSmokeTest {
    @Test
    void harnessWorks() {
        assertThat("allstate".toUpperCase()).isEqualTo("ALLSTATE");
    }
}
