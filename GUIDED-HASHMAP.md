# Build a HashMap — guided walkthrough

The single highest-value exercise in this repo. *"How does HashMap work internally?"* is
the most-reported question in Allstate's Java rounds, and they screen for the **mechanism**,
not a definition. Build it once and the question becomes free.

New to TDD? Read **[GUIDED-TDD.md](GUIDED-TDD.md)** first — it's 10 minutes.

---

## The mental model — read this twice

**A HashMap is an array of linked lists.** That's the whole secret.

```
        table (an array of 16 slots)
        ┌─────┐
   [0]  │  ●──┼──> null                              (empty slot)
        ├─────┤
   [1]  │  ●──┼──> ("Aa"=1) ──> ("BB"=2) ──> null    (2 keys COLLIDED here)
        ├─────┤
   [2]  │  ●──┼──> ("cat"=9) ──> null
        ├─────┤
   [3]  │  ●──┼──> null
        └─────┘
```

**To store `key -> value`:**
1. Turn the key into a number — `key.hashCode()`
2. Squash that number into a slot index — `hash & (capacity - 1)`
3. Hang the entry off that slot. Something already there? Add it to the chain.

**To look up a key:** steps 1 and 2 again, then walk that *one short chain*.

That's why it's O(1) on average: you never search the whole map, only one small chain.
And it's O(n) in the worst case: if every key lands in the same slot, you're walking a
linked list.

---

## What's already written for you

Open `src/main/java/com/allstate/prep/ds/MyHashMap.java`. The fiddly parts are done:

| Given | What it does |
|---|---|
| `Node` class | one entry: `hash`, `key`, `value`, `next` — `next` is what makes a chain |
| `table`, `size`, `threshold` | the bucket array and the bookkeeping |
| constructor | capacity 16, threshold 12 |
| `hash(key)` | key → well-spread number |
| `indexFor(hash, cap)` | number → slot index |
| `keysEqual(a, b)` | null-safe `.equals()` comparison |
| `findNode(key)` | the whole lookup: hash → slot → walk chain |
| `resize()` | doubling + re-placing every node |
| `size()`, `capacity()` | trivial accessors |

**Read `hash()` and `resize()` carefully — their comments are your interview answers.**

You write **four methods**. Run after every single one:

```bash
./mvnw -q test -Dtest=MyHashMapTest
```

---

## TODO 1 — `get()` · start here, ~1 line

Return the value, or `null` if the key isn't there. `findNode` already does the work:

```java
public V get(K key) {
    Node<K, V> n = findNode(key);
    return n == null ? null : n.value;
}
```

Run it. **2 tests go green** (`putThenGet` still fails — it needs `put`, which is TODO 3).

> Why so easy? Because `findNode` is the lookup. Getting that for free lets you focus on
> the part that actually teaches you something: insertion.

---

## TODO 2 — `containsKey()` · ~1 line

```java
public boolean containsKey(K key) {
    return findNode(key) != null;
}
```

**Why this can't just be `get(key) != null`:** a key can legitimately be *mapped to* `null`.
`get` would return `null` for both "absent" and "present but null", so it can't tell them
apart. `containsKey` can. That distinction is a genuine interview question — and it's
exactly why `ConcurrentHashMap` forbids null values altogether.

---

## TODO 3 — `put()` · the real exercise

Store `key -> value`, return the **previous** value or `null`. Five steps:

**Step 1 — find the slot**
```java
int h = hash(key);
int i = indexFor(h, table.length);
```

**Step 2 — is the key already here?** Walk the chain. If found, overwrite and return the old value:
```java
for (Node<K, V> n = table[i]; n != null; n = n.next) {
    if (n.hash == h && keysEqual(n.key, key)) {
        V old = n.value;
        n.value = value;
        return old;          // ⚠️ do NOT touch size — this replaced, not added
    }
}
```
> The `n.hash == h &&` before `keysEqual` is a **deliberate fast path**: comparing two ints
> is far cheaper than calling `equals()`, and unequal hashes can't be equal keys. Real
> HashMap does this. Worth saying aloud.

**Step 3 — new key, so insert at the FRONT of the chain**
```java
table[i] = new Node<>(h, key, value, table[i]);
```
> Prepending is O(1). Appending would mean walking to the chain's end first. The new node's
> `next` becomes whatever was in the slot — which is how `"Aa"` and `"BB"` coexist.

**Step 4 — grow if too full**
```java
size++;
if (size > threshold) resize();
```

**Step 5**
```java
return null;    // there was no previous value
```

Run it. **6 tests green**, including `handlesCollidingKeys` and `resizesPastTheLoadFactor…`.

### Two traps this catches

- **Bumping `size` on an overwrite.** Then `put("a",1); put("a",2)` reports size 2. The test
  `putReturnsPreviousValueAndOverwrites` exists precisely to catch it.
- **Forgetting `table[i]` as the new node's `next`.** Then every collision destroys the
  previous entry. `handlesCollidingKeys` catches it — `"Aa"` and `"BB"` have the **same
  `hashCode()` in Java**, so they always land in the same slot.

---

## TODO 4 — `remove()` · unlink instead of insert

Same start, but carry a `prev` pointer so you can splice a node out:

```java
public V remove(K key) {
    int h = hash(key);
    int i = indexFor(h, table.length);
    Node<K, V> prev = null;
    for (Node<K, V> n = table[i]; n != null; prev = n, n = n.next) {
        if (n.hash == h && keysEqual(n.key, key)) {
            if (prev == null) table[i] = n.next;   // it was the chain HEAD
            else prev.next = n.next;               // it was mid-chain
            size--;
            return n.value;
        }
    }
    return null;
}
```

**The classic bug** is skipping the `prev == null` branch and always doing `prev.next = …`.
That NPEs on the head, or — worse, if you write `table[i] = null` instead — silently deletes
every other key in that slot.

**All 8 tests green.** ✅

---

## Now you can answer the question

Say this, roughly in this order:

> "It's an array of nodes — buckets — where each bucket is a chain. On `put`, the key's
> `hashCode` is spread with `h ^ (h >>> 16)` so high bits influence the index, then the
> index is `hash & (capacity - 1)` — a bitmask, which is why capacity is always a power of
> two. If the bucket is empty we store the node; otherwise we walk the chain comparing hash
> first and then `equals`, overwriting on a match or prepending if it's new. Default capacity
> is 16 with a load factor of 0.75, so at 13 entries it doubles and re-places everything.
> Since Java 8, a bucket with 8+ nodes in a table of at least 64 converts to a red-black
> tree, so the worst case is O(log n) rather than O(n)."

Then the follow-ups you now own:

| They ask | You say |
|---|---|
| Why power-of-two capacity? | `hash & (cap-1)` replaces `%`, which needs a power of two |
| Why `h ^ (h >>> 16)`? | index only sees low bits; this mixes the high bits down |
| What's the load factor for? | keeps chains short so lookups stay O(1); 0.75 balances space vs collisions |
| What happens on collision? | chaining; Java 8 treeifies at 8, untreeifies at 6 |
| Worst-case complexity? | O(n) pre-Java-8, O(log n) since, from treeification |
| Why must keys be immutable? | a mutated key hashes elsewhere and becomes unreachable, though `size()` still counts it |
| How does `HashSet` relate? | it *is* a HashMap — `map.put(e, PRESENT)`, one shared sentinel value |
| Thread-safe? | no. Lost updates; in Java 7 a concurrent resize could build a circular chain and spin forever. Run `HashMapVsConcurrentHashMapDemo` — it loses ~100k of 160k writes |

---

## Checks

```bash
./mvnw -q test -Dtest=MyHashMapTest          # your work
diff <(cat reference-solutions/MyHashMap.java.txt) \
     src/main/java/com/allstate/prep/ds/MyHashMap.java
```

Stuck on one method? The reference is `reference-solutions/MyHashMap.java.txt` — but read
just the method you're stuck on, then close it.

**Next:** the same mechanism with no `java.util` at all → `MySet`, via
**[GUIDED-TDD.md](GUIDED-TDD.md)**. That's the one Allstate actually asks you to build live.
