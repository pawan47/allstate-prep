# TDD, explained from zero

If "write a failing test first" doesn't click yet, read this once. It's 10 minutes and
then the whole dojo makes sense.

---

## What TDD actually is

Three steps, in a loop. That's the entire method.

```
🔴 RED      Write ONE small test for something that doesn't exist yet. Run it. It fails.
🟢 GREEN    Write the LAZIEST code that makes it pass. Cheating is allowed here.
🔧 REFACTOR Clean up the cheat. Tests still pass. Repeat.
```

**Why Allstate cares:** they're an Extreme Programming shop. In your technical round an
interviewer gives you a requirement, and they watch *you* write the test before the code.
They're scoring the habit, not just the finished class.

**The two rules people break:**
1. Only **one** test at a time. Not five.
2. You must actually **run** the failing test and look at the error. Skipping this means
   you don't know whether the test can even detect the bug.

---

## Your very first cycle — follow along literally

We'll build `MySet` (a Set that stores unique items). Copy-paste exactly what's below.
Don't think ahead. The whole point is that each step is tiny.

### Setup: open two terminals

**Terminal 1** — the test runner. You'll press ↑ then Enter here, over and over:

```bash
cd /Users/pawan/ai/allstate-prep && ./mvnw -q test -Dtest=TddDrillTest
```

**Terminal 2** — your editor (or just use VS Code / IntelliJ).

---

### 🔴 Cycle 1, RED — the smallest possible test

Open `src/test/java/com/allstate/prep/ds/TddDrillTest.java`. **Delete everything** in it
and paste this:

```java
package com.allstate.prep.ds;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class TddDrillTest {

    @Test
    void aNewSetIsEmpty() {
        MySet<String> set = new MySet<>();
        assertThat(set.size()).isZero();
    }
}
```

That's the whole first test. It says: *a brand-new set has size 0.* Nothing clever.

Now run Terminal 1. **You should see it FAIL**, something like:

```
UnsupportedOperationException: TDD: write the test first, then build me.
```

🎉 **That failure is success.** You've proven the test runs and that the feature is
genuinely missing. This is the step everyone skips — don't.

---

### 🟢 Cycle 1, GREEN — the laziest possible code

Open `src/main/java/com/allstate/prep/ds/MySet.java`. Replace the **constructor** and
**`size()`** with this. Leave the other methods alone:

```java
public MySet() {
    // nothing to do yet
}

public int size() {
    return 0;          // ← yes, hardcoded. This is legal in GREEN.
}
```

Run Terminal 1 again. **It passes.** ✅

Returning a hardcoded `0` feels like cheating, and that's fine — the next test will force
you to make it honest. That pressure is the engine of TDD.

---

### 🔴 Cycle 2, RED — force the cheat out

Add a **second** test below the first (keep the first one):

```java
@Test
void addingOneItemMakesSizeOne() {
    MySet<String> set = new MySet<>();
    set.add("a");
    assertThat(set.size()).isEqualTo(1);
}
```

Run it. It fails — `size()` returns 0 but the test wants 1. Your hardcoded cheat just got
caught, **by your own test**. That's the loop working.

---

### 🟢 Cycle 2, GREEN — make it real

Now you need somewhere to put things and something to count. Add a field, and make
`add`/`size` use it:

```java
private Object[] items = new Object[16];   // our storage
private int size;                          // real count now

public boolean add(E element) {
    items[size] = element;
    size++;
    return true;
}

public int size() {
    return size;        // honest at last
}
```

Run it. **Both tests pass.** ✅

You now have a working (if naive) Set and, more importantly, you've done the loop twice.

---

### 🔴 Cycle 3, RED — the next requirement

A Set must not hold duplicates. Write the test that demands it:

```java
@Test
void addingTheSameItemTwiceDoesNotGrowTheSet() {
    MySet<String> set = new MySet<>();
    set.add("a");
    set.add("a");
    assertThat(set.size()).isEqualTo(1);
}
```

Fails — your `add` blindly appends, so size is 2.

### 🟢 Cycle 3, GREEN

```java
public boolean add(E element) {
    if (contains(element)) return false;    // a Set ignores duplicates
    items[size] = element;
    size++;
    return true;
}

public boolean contains(E element) {
    for (int i = 0; i < size; i++) {
        if (items[i].equals(element)) return true;
    }
    return false;
}
```

Passes. ✅ Notice the scan is O(n) and slow — **that's fine for now.** Making it fast is a
*later* cycle, and TDD's whole discipline is not doing it yet.

---

## You've got it

That's TDD. Test → fail → minimal code → pass → repeat. The remaining cycles are just more
of the same:

| Cycle | Write a test that says… | Which forces you to… |
|---|---|---|
| 4 | `contains` on a missing item is false | handle the not-found case |
| 5 | `remove("a")` makes size 0 | shift or unlink the slot |
| 6 | two equal `new String("hi")` count as one | use `.equals()`, not `==` |
| 7 | `add(null)` works | special-case a null hash |
| 8 | 1000 items are all still found | **grow** the array (resize) |
| 9 | `"Aa"` and `"BB"` both survive | handle **hash collisions** ⭐ |

**Cycle 9 is the interview payoff.** `"Aa"` and `"BB"` have the *same* `hashCode()` in
Java, so once you switch from a linear scan to hash buckets, they collide. Handling that
is exactly what "how does HashMap work internally?" is asking about.

When you're done, check yourself against the full spec:

```bash
./mvnw -q test -Dtest=MySetSpecTest
```

Any of its 10 tests you didn't think of is a gap. That list is worth more than the code.

---

## Say it out loud

In the real round you're the **driver** and you narrate continuously. Silence reads as
"can't pair." Practise these:

- "I'll start with the simplest behaviour — a new set has size zero."
- "Running it… it fails because `size()` doesn't exist yet. That's the failure I expected."
- "I'm hardcoding zero to get green, then the next test will force me to make it real."
- "This scan is O(n). I'll live with it and fix it once I have correctness."
- "The edge case I'm thinking about now is hash collisions — `Aa` and `BB` collide in Java."

---

## Stuck?

- **`UnsupportedOperationException`** → normal. It means you haven't written that method yet.
- **Test won't compile** → check the `package` line and that you kept the imports.
- **Everything passes immediately** → your test isn't actually testing anything. Break the
  code on purpose and confirm the test goes red.
- **Want to see a finished version?** `reference-solutions/MySet.java.txt`. Read it *after*
  you've tried, and read the comments — they hold the "why" answers.

Next: the same loop, applied to a HashMap → **[GUIDED-HASHMAP.md](GUIDED-HASHMAP.md)**.
