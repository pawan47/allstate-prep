# Day 1 — Core Java

It's ~17:30 as this was built, so this is laid out as **duration blocks, not clock times**.
Two routes:

- **Full route (6h)** — all 6 blocks. Do this if you have tomorrow morning too.
- **Tonight route (3h 30m)** — Blocks 1, 2, 4, 6. This covers every question actually reported from Allstate's rounds. If you only do one thing, do Block 2.

> Rule for the whole day: **nothing gets "read."** Every item is either typed as code or said out loud. Reading Java notes feels like progress and isn't — the L1 round is 2 hours of being asked to *produce*, not recognise.

---

## Block 1 — Prove the setup works (10 min)

```bash
./mvnw -q test -Dtest=SetupSmokeTest
```

Green? Good. Now look at what's here:

- `src/main/java/.../ds/` — `MySet`, `MyHashMap`, `LRUCache` — **stubs you implement**
- `src/main/java/.../strings/`, `arrays/` — the reported Allstate coding problems, stubs
- `src/test/java/.../` — full reference tests, already written, so you always know if you're right
- `src/test/java/.../ds/TddDrillTest.java` — **empty on purpose. This is yours.**
- `src/main/java/.../demos/` — 4 runnable programs that prove the collections/concurrency answers
- `reference-solutions/` — `.java.txt` files. Not compiled, so you can't peek by accident. Check *after*.

---

## Block 2 — `MySet` by TDD, no collections (75 min) ⭐ **THE ONE THAT MATTERS**

This is Allstate's signature technical exercise, reported almost verbatim by candidates:
*"implement the functionality of Set without using any collections, and the interviewer will do the TDD and ask questions about how to implement and pass the test cases."*

> ### 🆕 Never done TDD, or not sure what the first step is?
> **Read [GUIDED-TDD.md](GUIDED-TDD.md) first.** It walks you through the first three
> red→green cycles with the exact code to paste, the exact error you should see, and why.
> 10 minutes, and then this block makes sense.

**Do it the interview way.** Open `TddDrillTest.java`, delete the `@Disabled`, and run this on a loop in a second terminal:

```bash
./mvnw -q test -Dtest=TddDrillTest
```

Work one cycle at a time. **Do not write `MySet` ahead of your tests.**

| # | Cycle | Test you write first | Then implement |
|---|-------|---------------------|----------------|
| 1 | empty | `size()` is 0, `isEmpty()` true | fields only |
| 2 | add | `add("a")` returns `true` | bucket array + insert |
| 3 | contains | `contains("a")` true, `contains("b")` false | hash → index → scan chain |
| 4 | duplicate | second `add("a")` returns `false`, size stays 1 | check before insert |
| 5 | equals | `new String("hello")` found by an equal instance | `equals()`, **not** `==` |
| 6 | collision | `"Aa"` and `"BB"` both survive (same `hashCode` in Java!) | chaining |
| 7 | remove | returns whether present; chain stays intact | unlink node |
| 8 | null | `add(null)` works once | null-hash special case |
| 9 | resize | 1000 elements all still found | grow + rehash at load factor |

Constraints: **no `java.util.*` at all.** `Object[]` buckets and a `Node` class you write.

Then, and only then:

```bash
./mvnw -q test -Dtest=MySetSpecTest
```

**Every test in the spec that you didn't think of yourself is a gap.** Write them down — that list is exactly what the interviewer will find if you don't.

### While you code, say these out loud (this is scored)
- "Simplest behaviour first: an empty set has size zero."
- "This fails because the field doesn't exist yet — that's the right failure."
- "I'll hardcode `true` to get green, then generalise."
- "Next I'm worried about hash collisions — `Aa` and `BB` collide in Java, let me test that."
- "Average O(1), worst case O(n) per bucket unless I treeify like Java 8 does."

### Then: `MyHashMap` — now fully guided

Follow **[GUIDED-HASHMAP.md](GUIDED-HASHMAP.md)**.

I've rewritten this one to be much gentler after your feedback. The fiddly parts — the
`Node` class, the fields, `hash()`, `indexFor()`, `findNode()` and `resize()` — are **already
written for you, with comments that double as your interview answers**. You implement just
four methods, in increasing difficulty:

| | Method | Effort |
|---|---|---|
| TODO 1 | `get()` | ~1 line — start here |
| TODO 2 | `containsKey()` | ~1 line |
| TODO 3 | `put()` | the real exercise, broken into 5 numbered steps |
| TODO 4 | `remove()` | like put, plus unlinking |

```bash
./mvnw -q test -Dtest=MyHashMapTest
```

I verified that following those four TODOs exactly as written makes all 8 tests pass, so if
you're stuck it's a detail, not a missing instruction.

This is the highest-leverage item in the whole repo: *"how does HashMap work internally?"* is
the most-reported Allstate Java question, and building it once means you answer from the
mechanism instead of from memory.

---

## Block 3 — The four proof demos (45 min)

Run each, read the output, then **close the terminal and say the answer out loud from memory.**

```bash
cd allstate-prep
./mvnw -q compile
./mvnw -q exec:java -Dexec.mainClass=com.allstate.prep.demos.FailFastVsFailSafeDemo
./mvnw -q exec:java -Dexec.mainClass=com.allstate.prep.demos.HashMapVsConcurrentHashMapDemo
./mvnw -q exec:java -Dexec.mainClass=com.allstate.prep.demos.WaitVsSleepDemo
./mvnw -q exec:java -Dexec.mainClass=com.allstate.prep.demos.ShallowVsDeepCopyDemo
```

Each maps to reported Allstate questions:

| Demo | Reported question it answers |
|------|------------------------------|
| `FailFastVsFailSafeDemo` | "What is fail-safe and fail-fast?" · "What happens when we iterate over concurrent hashmap and hashmap?" |
| `HashMapVsConcurrentHashMapDemo` | "Difference between concurrent hashmap and hashmap?" · "Why to use concurrent hashmap?" · "How hash set works?" |
| `WaitVsSleepDemo` | "What is difference between wait and sleep?" |
| `ShallowVsDeepCopyDemo` | "What is shallow and deep copy?" · "In how many ways can an object be instantiated?" |

The HashMap one is the money shot: **it loses ~100,000 writes in front of you.** "I've watched a plain HashMap silently drop two-thirds of its writes under 8 threads" is a far better answer than "it's not thread-safe."

---

## Block 4 — Rotated array, out loud, timed (40 min)

Reported *verbatim* from Allstate: *"Write a program to find the index of '3' in a rotated array with the best algorithm"* and *"What is a pivot in a rotated array and how to find it?"*

The phrase **"best algorithm"** is the whole question. A linear scan returns the right answer and fails the round.

```bash
./mvnw -q test -Dtest=ArrayProblemsTest
```

Do `findPivot` first (it's the easier binary search), then `searchRotated`. **Set a 20-minute timer** and narrate throughout — this is the coding-round rehearsal, not just the problem.

Before you type a line, say: *"Sorted-but-rotated means at any midpoint, at least one half is properly sorted. I check which half is sorted, then check whether the target lies inside that half's range — if yes I go there, if no I go to the other half. O(log n)."*

---

## Block 5 — Strings + the rest of the array bank (60 min)

```bash
./mvnw -q test -Dtest=StringProblemsTest
./mvnw -q test -Dtest=LRUCacheTest
```

Priority order by reported frequency:
1. **`lengthOfLongestSubstring`** — named directly in Allstate's tech screen
2. **`LRUCache`** — named directly ("LRU Cache design/code problems"). HashMap + doubly-linked list, both ops O(1). Build it by hand; mention `LinkedHashMap(accessOrder=true)` only *after*.
3. `mergeIntervals`, `maxSubArrayLen`, `compress` — all on the Allstate LeetCode tag list
4. `reverseWords`, `firstUniqueChar`, `twoSum`, `rotate` — warm-ups

Allstate's LeetCode tag has **31 problems: 8 easy / 16 medium / 7 hard**, weighted toward
**arrays, strings and hash tables**. The full list with links is in
**[leetcode/ALLSTATE-LEETCODE.md](leetcode/ALLSTATE-LEETCODE.md)** — work it top-down by frequency.

> **Correction to something I said earlier:** I claimed graphs and DP were wasted effort.
> The actual tag list is broader than that — 4 tree problems, 3 linked list, 2 stack, 4 heap
> and 2 DP. Arrays/strings still dominate the top, but don't assume the tail is empty.
> Union Find (1 problem) is the only thing genuinely safe to skip.

---

## Block 6 — Q&A out loud (40 min)

Open **[cheatsheets/01-core-java-qbank.md](cheatsheets/01-core-java-qbank.md)** — every question in it is one really asked at Allstate, with the answer at the depth they screen for.

Method: cover the answer, say yours out loud, *then* reveal. Anything you fumble goes on a sticky note for Day 7.

Then **[cheatsheets/02-java8-and-modern.md](cheatsheets/02-java8-and-modern.md)** — "Features of Java 7?" is on their list, which means version-history questions are in play, and for a 3–5 yr role Java 8 streams/optionals/functional interfaces are guaranteed.

---

## End-of-day check

You're done when you can, from a blank file and with no notes:

- [ ] Build a working `Set` with no `java.util`, tests first, narrating
- [ ] Explain HashMap's bucket array, hash spreading, load factor 0.75, treeify at 8
- [ ] State 5 differences between `HashMap` and `ConcurrentHashMap` without pausing
- [ ] Explain fail-fast vs fail-safe *and* the legal way to remove during iteration
- [ ] Give the `wait`/`sleep` table, including "always wait in a `while` loop"
- [ ] Explain shallow vs deep copy and name 4 ways to deep copy
- [ ] Write `searchRotated` in under 10 minutes, correct on first run
- [ ] Write `LRUCache` with O(1) get and put
- [ ] Name all 6 ways to instantiate an object, and which 2 skip the constructor

Tick 7 of 9 and Day 1 did its job. Tomorrow is Spring Boot.
