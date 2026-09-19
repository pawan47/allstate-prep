# Reference solutions — open these AFTER you've tried

These are `.java.txt`, not `.java`, on purpose: they are **not** on the compile path, so
you can't accidentally peek by running the tests, and they can't collide with your own work.

**All five were verified against the full 71-test suite: 0 failures.** So if your version
disagrees with a test, the test is right.

| File | Problem |
|---|---|
| `MySet.java.txt` | Set with **zero `java.util`** — Allstate's signature TDD exercise |
| `MyHashMap.java.txt` | HashMap from scratch — buckets, hash spreading, load factor, resize |
| `LRUCache.java.txt` | HashMap + doubly-linked list, O(1) get and put |
| `StringProblems.java.txt` | longest substring, compression, reverse words, first unique |
| `ArrayProblems.java.txt` | rotated search, pivot, merge intervals, max-subarray-sum-k, two sum, rotate |

To diff your attempt against the reference:

```bash
diff <(cat reference-solutions/MySet.java.txt) src/main/java/com/allstate/prep/ds/MySet.java
```

Read the comments as much as the code — they contain the *"why"* lines that are the
actual answers to the follow-up questions ("why is capacity a power of two?", "why
`putIfAbsent` and not `put`?", "why compare against `nums[hi]`?").
