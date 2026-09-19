package com.allstate.prep.ds;

import org.junit.jupiter.api.*;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("LRU Cache — REPORTED IN ALLSTATE'S TECHNICAL SCREEN")
class LRUCacheTest {

    @Test
    void getsWhatYouPut() {
        LRUCache c = new LRUCache(2);
        c.put(1, 1);
        assertThat(c.get(1)).isEqualTo(1);
    }

    @Test
    void missingKeyReturnsMinusOne() {
        assertThat(new LRUCache(2).get(42)).isEqualTo(-1);
    }

    @Test
    void evictsLeastRecentlyUsedOnOverflow() {
        LRUCache c = new LRUCache(2);
        c.put(1, 1);
        c.put(2, 2);
        c.put(3, 3);              // capacity exceeded -> key 1 is the LRU
        assertThat(c.get(1)).isEqualTo(-1);
        assertThat(c.get(2)).isEqualTo(2);
        assertThat(c.get(3)).isEqualTo(3);
    }

    @Test
    void aGetCountsAsUseAndSavesTheEntry() {
        LRUCache c = new LRUCache(2);
        c.put(1, 1);
        c.put(2, 2);
        c.get(1);                 // 1 is now the most recent, so 2 is the LRU
        c.put(3, 3);
        assertThat(c.get(2)).isEqualTo(-1);
        assertThat(c.get(1)).isEqualTo(1);
    }

    @Test
    void updatingExistingKeyDoesNotGrowOrEvict() {
        LRUCache c = new LRUCache(2);
        c.put(1, 1);
        c.put(2, 2);
        c.put(1, 100);            // update, not insert
        assertThat(c.get(1)).isEqualTo(100);
        assertThat(c.get(2)).isEqualTo(2);
    }

    @Test
    void updatingExistingKeyAlsoRefreshesRecency() {
        LRUCache c = new LRUCache(2);
        c.put(1, 1);
        c.put(2, 2);
        c.put(1, 100);            // 2 becomes the LRU
        c.put(3, 3);
        assertThat(c.get(2)).isEqualTo(-1);
        assertThat(c.get(1)).isEqualTo(100);
    }

    @Test
    void capacityOfOneAlwaysHoldsTheNewest() {
        LRUCache c = new LRUCache(1);
        c.put(1, 1);
        c.put(2, 2);
        assertThat(c.get(1)).isEqualTo(-1);
        assertThat(c.get(2)).isEqualTo(2);
    }
}
