# Allstate Core Java Question Bank

**Every question marked 🎯 was reported by an actual Allstate candidate** (GeeksforGeeks Allstate interview page, Glassdoor Allstate/Allstate India, interview guides). The rest are the follow-ups that inevitably come next.

Drill method: cover the answer, say yours **out loud**, then reveal. Answers are written at the depth Allstate screens for — they explicitly look for *internal mechanism*, not definitions.

---

## 1. Collections internals

### 🎯 "How does HashSet work?"

`HashSet` **is** a `HashMap`. That's the whole answer, and it's one line of real JDK source:

```java
private transient HashMap<E,Object> map;
private static final Object PRESENT = new Object();

public boolean add(E e) { return map.put(e, PRESENT) == null; }
public boolean contains(Object o) { return map.containsKey(o); }
public boolean remove(Object o) { return map.remove(o) == PRESENT; }
```

Everything they ask next falls out of it:
- **Uniqueness** comes from HashMap key uniqueness → `hashCode()` then `equals()`
- **`add` returns false** on a duplicate because `put` returned the existing (non-null) value
- **No ordering**, because HashMap has none (`LinkedHashSet` = `LinkedHashMap`, insertion order; `TreeSet` = `TreeMap`, sorted)
- **One null element**, because HashMap allows one null key
- **O(1) average**, O(log n) worst case since Java 8 treeifies bins
- A single shared `PRESENT` sentinel, so there's no per-element value overhead

> **Follow-up they always ask:** *what happens if you mutate an object after adding it to a HashSet?* → Its `hashCode` changes, so it now hashes to a different bucket. It becomes **unreachable**: `contains()` returns false, `remove()` fails, but it's still in the set and still counts toward `size()`. This is why set/map keys should be **immutable**.

---

### "How does HashMap work internally?" *(most-reported Java question in the whole loop)*

**The storage:** an array `Node<K,V>[] table`, where each `Node` holds `hash, key, value, next`.

**The constants** — memorise these four numbers:
| Constant | Value |
|---|---|
| `DEFAULT_INITIAL_CAPACITY` | 16 (always a power of 2) |
| `DEFAULT_LOAD_FACTOR` | 0.75 → resize threshold = capacity × 0.75 |
| `TREEIFY_THRESHOLD` | 8 |
| `UNTREEIFY_THRESHOLD` | 6 |

**`put(k,v)` step by step:**
1. **Spread the hash:** `(h = key.hashCode()) ^ (h >>> 16)`
   *Why?* The index is computed as `hash & (capacity - 1)`, which only looks at the **low** bits. XOR-ing the high 16 bits down means a `hashCode` that differs only in its high bits still lands in a different bucket. This one detail is the single best thing you can say here.
2. **Index:** `i = (n - 1) & hash` — a bitmask, not a modulo, which is why capacity is always a power of 2.
3. **Empty bucket?** Store the node, done.
4. **Occupied?** Walk the chain. For each node: if `hash` matches **and** (`key == k` or `key.equals(k)`), overwrite the value and return the old one.
5. **Not found?** Append. If the chain length reaches **8** *and* the table is at least **64** long, convert the bin to a **red-black tree** (`TreeNode`) → worst case drops from O(n) to O(log n). If the table is shorter than 64, it **resizes instead** of treeifying.
6. **`++size > threshold`?** Resize: double capacity and redistribute.

**Resize in Java 8+:** the clever bit — it doesn't recompute hashes. When capacity doubles, an entry's new index is either the same `i` or `i + oldCap`, decided by a single bit test (`hash & oldCap`). Each bin is split into a "lo" and "hi" list, **preserving relative order**.

**Null key:** hashed to 0, so it always lives in bucket 0. Exactly one allowed.

**Not thread-safe:**
- In **Java 7**, resize reversed the chain, so two threads resizing concurrently could form a **circular linked list** → `get()` spins forever, pegging a CPU at 100%. A genuinely famous production outage class.
- **Java 8** preserves order, killing the infinite loop, but you still get **lost updates**, a wrong `size()`, and torn reads. Run `HashMapVsConcurrentHashMapDemo` — it drops ~2/3 of 160,000 writes.

---

### 🎯 "Difference between HashMap and ConcurrentHashMap?" · 🎯 "Why use ConcurrentHashMap?"

| | `HashMap` | `Hashtable` | `ConcurrentHashMap` |
|---|---|---|---|
| Thread-safe | ❌ | ✅ | ✅ |
| Locking | none | one lock, **every method** | Java 8: **per-bin** (CAS + `synchronized` on bin head). Java 7: 16 lock stripes (`Segment`) |
| Reads | — | locked | **lock-free** volatile reads |
| Null key | 1 allowed | ❌ | ❌ |
| Null values | ✅ | ❌ | ❌ |
| Iterator | fail-fast (CME) | fail-fast | **weakly consistent**, never CME |
| Compound atomics | — | — | `putIfAbsent`, `computeIfAbsent`, `merge`, `replace` |
| Use it? | single-threaded | **never** (legacy) | concurrent |

**Why ConcurrentHashMap, in one sentence:** it gives you correctness under concurrency *without* serialising every thread, because it locks a single bin rather than the whole map, and doesn't lock reads at all.

**Why it forbids nulls** — this is a great answer because most people don't know it: in a map that other threads are mutating, a `null` from `get()` would be ambiguous between *"no mapping"* and *"mapped to null"*, and you **cannot** disambiguate with `containsKey()` because the map may change between your two calls. HashMap can afford the ambiguity; a concurrent map can't.

**Two more details that land well:**
- `size()` is an **estimate** under concurrent writes (it sums `baseCount` + a striped `CounterCell[]`). Use `mappingCount()` — it returns `long` and is honest about being approximate.
- `Collections.synchronizedMap(new HashMap<>())` is correct but takes **one global lock**, and you *still* need external synchronisation for compound operations like get-then-put. ConcurrentHashMap's `computeIfAbsent` makes that atomic for you.

---

### 🎯 "What happens when we iterate over a HashMap vs a ConcurrentHashMap?"

- **HashMap** → **fail-fast**. The iterator snapshots `modCount` when created and re-checks it on every `next()`. Any structural change (`put` of a new key, `remove`, `clear`) bumps `modCount` → **`ConcurrentModificationException`**. Note this fires even **single-threaded** — the classic bug is modifying inside a for-each loop.
- **ConcurrentHashMap** → **weakly consistent**. The iterator reads live bins, tolerates concurrent writes, reflects everything present when it started, and *may or may not* reflect later changes. It **never** throws CME.

**The legal way to remove while iterating** (they like asking this):
```java
Iterator<Entry<K,V>> it = map.entrySet().iterator();
while (it.hasNext()) {
    if (shouldDrop(it.next())) it.remove();   // iterator.remove() syncs modCount
}
// or, one line:
map.entrySet().removeIf(e -> shouldDrop(e));
```

---

### 🎯 "What is fail-fast and fail-safe?"

**Fail-fast** — `ArrayList`, `HashMap`, `HashSet`, `LinkedList`. Track `modCount`, throw `ConcurrentModificationException` on detected concurrent modification. Crucially: it's a **best-effort bug detector**, *not* a synchronisation guarantee. The Javadoc says so explicitly — never write logic that depends on it firing.

**Fail-safe** — never throws, by not iterating the live data:
- `CopyOnWriteArrayList` / `CopyOnWriteArraySet` — iterate an **immutable snapshot**. Writes copy the entire backing array. Great for read-heavy, small, rarely-written lists (listener registries). Terrible for write-heavy.
- `ConcurrentHashMap`, `ConcurrentLinkedQueue` — weakly consistent, live but tolerant.

**Trade-off to state:** fail-safe buys you no exception at the cost of **staleness** (and, for copy-on-write, O(n) per write).

Run `FailFastVsFailSafeDemo` to see all five cases fire.

---

### Collections quick-fire (expect several of these in L1)

- **`ArrayList` vs `LinkedList`** — ArrayList: contiguous `Object[]`, O(1) random access, O(n) insert-in-middle, grows by ~1.5×. LinkedList: doubly-linked nodes, O(1) insert *given a node*, but O(n) to *find* it, and terrible cache locality. **In practice ArrayList wins nearly always**; use LinkedList when you need `Deque`. Say that — it shows judgment.
- **`HashMap` vs `LinkedHashMap` vs `TreeMap`** — no order / insertion (or access) order / sorted by comparator. TreeMap is O(log n) and can't take null keys.
- **`equals`/`hashCode` contract** — equal objects **must** have equal hash codes; unequal objects *may* collide. Break it and your object gets lost in every hash collection. Override both, together, always.
- **`Comparable` vs `Comparator`** — natural ordering inside the class (`compareTo`) vs external, multiple orderings (`compare`). `Comparator.comparing(...).thenComparing(...)` is the modern form.
- **`Iterator` vs `ListIterator`** — forward-only vs bidirectional + `add`/`set`.
- **Fast-path:** `Arrays.asList()` is **fixed-size** and backed by the array; `List.of()` is **truly immutable** and rejects nulls.

---

## 2. Concurrency

### 🎯 "Difference between `wait()` and `sleep()`?"

|  | `wait()` | `sleep()` |
|---|---|---|
| Declared on | `java.lang.Object` | `java.lang.Thread` |
| Static | no — acts on a monitor | yes — acts on current thread |
| **Monitor lock** | **releases it** | **keeps it** |
| Must hold the lock? | **yes** → else `IllegalMonitorStateException` | no |
| Woken by | `notify()`/`notifyAll()`, timeout, interrupt | timeout, interrupt |
| Purpose | inter-thread coordination | pure delay / throttle |
| Thread state | `WAITING` / `TIMED_WAITING` | `TIMED_WAITING` |

**Always `wait()` in a `while`, never an `if`:**
```java
synchronized (lock) {
    while (!conditionMet) lock.wait();   // guards against spurious wakeups
    proceed();
}
```
Spurious wakeups are **permitted by the JLS**. An `if` here is a real, ships-to-prod bug.

**The senior close:** *"In modern code I'd rarely write either — `BlockingQueue` for producer/consumer, `CountDownLatch`/`CyclicBarrier` for rendezvous, `Condition.await()/signal()` when I need multiple wait sets on one lock."*

Run `WaitVsSleepDemo` for the proof.

---

### Concurrency follow-ups you should expect

- **`volatile` vs `synchronized`** — `volatile` gives **visibility** and ordering (a happens-before edge), no atomicity. `volatile int i; i++` is still a race, because that's read-modify-write. `synchronized` gives **both** mutual exclusion and visibility. For atomic counters use `AtomicInteger` (CAS-based, lock-free).
- **`notify()` vs `notifyAll()`** — one arbitrary waiter vs all. Prefer `notifyAll()` unless you can prove all waiters are interchangeable; `notify()` with heterogeneous waiters causes **lost wakeups**.
- **Deadlock** — 4 conditions (mutual exclusion, hold-and-wait, no preemption, circular wait). Prevent by **global lock ordering** and `tryLock` with timeout. Detect with a thread dump (`jstack`) — it literally prints "Found one Java-level deadlock."
- **`Runnable` vs `Callable`** — `run()` returns void and can't throw checked exceptions; `call()` returns `V` and can throw. `Callable` + `ExecutorService.submit()` → `Future`.
- **`Future` vs `CompletableFuture`** — `Future.get()` blocks and doesn't compose; `CompletableFuture` chains (`thenApply`, `thenCompose`, `allOf`) and handles errors (`exceptionally`). *Kafka `Future` objects come up in their L2 round — know `producer.send()` returns `Future<RecordMetadata>` and that calling `.get()` on it makes the send synchronous and kills throughput.*
- **`ExecutorService`** — `newFixedThreadPool`, `newCachedThreadPool`, `newSingleThreadExecutor`, `newVirtualThreadPerTaskExecutor` (Java 21). Always `shutdown()`; know the difference from `shutdownNow()`. Never use `newCachedThreadPool` on unbounded input.
- **`synchronized` vs `ReentrantLock`** — the Lock adds `tryLock`, timeouts, interruptible acquisition, fairness, and multiple `Condition`s. Cost: you **must** `unlock()` in a `finally`.
- **`ThreadLocal`** — per-thread storage. Warn about leaks in thread pools: always `remove()` in a `finally`.

---

## 3. Object semantics

### 🎯 "What is shallow and deep copy?"

**Shallow** — fields copied as-is. Primitives by value, but every **reference** field still points at the *same* object. `Object.clone()` is shallow by default. Mutating a nested object through the copy is visible through the original.

**Deep** — the whole reachable object graph is duplicated. Fully independent.

**Ways to deep copy:**
1. **Copy constructor**, hand-written — fastest, explicit, the default choice
2. Override `clone()` and clone nested fields — but `Cloneable` is a **broken interface**: it's a marker with no `clone` method, `Object.clone` is `protected`, and it bypasses constructors. *Effective Java* says avoid it.
3. **Serialize + deserialize** (Java serialization, Jackson, Gson, `SerializationUtils.clone()`) — generic and easy, slow, needs everything serialisable
4. **Make it immutable instead** ← the real senior answer. If `Address` has final fields and no setters, sharing the reference is safe, so a shallow copy is all you ever need. `record` makes this nearly free.

**Trap:** `new ArrayList<>(other)` and `List.copyOf(other)` are **shallow for the elements**. A copied `List<Employee>` shares the `Employee` objects.

---

### 🎯 "In how many ways can an object be instantiated?"

Six. Most candidates name two.

1. **`new`** — `new Employee()`
2. **Reflection, no-arg** — `Class.forName("X").getDeclaredConstructor().newInstance()` *(`Class.newInstance()` is deprecated since Java 9 because it swallows constructor exceptions)*
3. **Reflection, any constructor** — `Constructor.newInstance(args)`
4. **`clone()`** — ⚠️ **no constructor runs**
5. **Deserialization** — ⚠️ **no constructor runs**
6. **Factory / builder methods** — `List.of()`, `Integer.valueOf()`, `Optional.of()` *(delegates to `new` internally, but naming it shows design sense)*

**The point worth making:** #4 and #5 **bypass the constructor**, so they can break invariants your constructor enforces. That's exactly why a singleton must defend itself — use an `enum` singleton, or throw from `readResolve()`.

---

### Other object-model questions

- **`==` vs `equals()`** — reference identity vs logical equality. `Integer` caches −128..127, so `Integer a=127, b=127; a==b` is `true` but at 128 it's `false`. Great trick question; know it.
- **`String` immutability** — enables the string pool, safe sharing across threads, and cached `hashCode`. `intern()` puts a runtime string in the pool. `new String("x") != "x"`.
- **`String` vs `StringBuilder` vs `StringBuffer`** — immutable / mutable & unsynchronised / mutable & synchronised. Concatenating in a loop with `+` is O(n²); use `StringBuilder`.
- **`final` vs `finally` vs `finalize`** — modifier / always-runs block / the deprecated (Java 9) and **removed** (Java 18) GC hook. Use `try-with-resources` or `Cleaner`.
- **Overloading vs overriding** — compile-time by signature vs runtime by dynamic dispatch. Overriding rules: same signature, return type covariant, access **not** narrower, **no broader checked exceptions**. `static` and `private` methods are **hidden, not overridden**.
- **Abstract class vs interface** — state + constructors + any access vs multiple inheritance of type. Since Java 8 interfaces have `default`/`static` methods; since 9, `private` ones. Choose interface for capability, abstract class for shared state.
- **Checked vs unchecked** — `Exception` vs `RuntimeException`. Checked = recoverable and part of the contract; unchecked = programming error. Never swallow an exception with an empty catch.
- **Marker interface** — no methods, pure metadata: `Serializable`, `Cloneable`, `RandomAccess`. Modern equivalent: annotations.

---

## 4. 🎯 Build & CI: "What is the use of Maven and Jenkins?"

**Maven** — declarative build + dependency management, driven by `pom.xml`.
- **Solves:** transitive dependencies, a standard directory layout, a standard **lifecycle** (`validate → compile → test → package → verify → install → deploy`), and reproducible builds.
- **Repos:** local `~/.m2/repository` → central → your org's remote (Nexus/Artifactory).
- **Scopes:** `compile` (default), `provided`, `runtime`, `test`, `system`, `import`.
- **Know:** `mvn dependency:tree` for resolving version conflicts (Maven uses **nearest-definition wins**), `<dependencyManagement>` to pin versions, profiles for per-environment builds.
- **vs Gradle:** Gradle is imperative/Groovy-Kotlin DSL, incremental, with a build cache and daemon — faster on big projects; Maven is more rigid and therefore more predictable.

**Jenkins** — CI/CD orchestration. Triggers on SCM webhooks, runs your pipeline, distributes work to agents.
- **`Jenkinsfile`**, declarative: `pipeline { agent … stages { stage('Build'){…} stage('Test'){…} stage('Deploy'){…} } post { always {…} } }` — committed to the repo, so the pipeline is versioned with the code.
- Typical stages: checkout → build → unit test → SonarQube/coverage gate → package → container image → deploy to env → smoke test.

> ⚠️ **Allstate-specific:** their rounds name **Azure DevOps CI/CD**, not Jenkins. Be ready for both. Azure DevOps mapping: `azure-pipelines.yml`, stages/jobs/steps, service connections, variable groups + Key Vault, self-hosted vs Microsoft-hosted agents, artifact feeds, environments with approval gates. If your experience is Jenkins, say so and then map the concepts across — showing the concepts transfer is the answer they want.

---

## 5. 🎯 Java version history

### "Features of Java 7?"
- **try-with-resources** (`AutoCloseable`) — auto resource management, suppressed exceptions
- **Multi-catch** — `catch (IOException | SQLException e)`, and precise rethrow
- **Diamond operator** — `Map<String,List<Integer>> m = new HashMap<>();`
- **Strings in `switch`**
- **Numeric literals** — binary `0b1010`, underscores `1_000_000`
- **NIO.2** — `java.nio.file`: `Path`, `Files`, `WatchService`, symlink support
- **Fork/Join framework** — `ForkJoinPool`, work-stealing (the engine under parallel streams later)
- **`invokedynamic`** bytecode — added for dynamic languages, became the foundation lambdas were built on in Java 8
- **G1 GC** shipped

### Java 8 (guaranteed to come up — see [02-java8-and-modern.md](02-java8-and-modern.md))
Lambdas, Streams, `Optional`, functional interfaces, `default`/`static` interface methods, method references, new `java.time`, `CompletableFuture`, PermGen → **Metaspace**.

### Worth one line each
- **11 (LTS)** — `var` in lambda params, new `HttpClient`, `String.strip/lines/repeat`, single-file source launch. **JAX-WS/JAXB removed from the JDK** (matters for the SOAP questions below).
- **17 (LTS)** — sealed classes, records (16), pattern matching for `instanceof`, text blocks (15), switch expressions (14).
- **21 (LTS)** — **virtual threads**, pattern matching for `switch`, sequenced collections, structured concurrency (preview).

---

## 6. 🎯 SOAP (Allstate is an insurer — real legacy SOAP services exist)

Four SOAP questions are on the reported list. 20 minutes here is cheap insurance.

**"What is a WSDL file?"** — Web Services Description Language: the machine-readable **XML contract** for a SOAP service. Five parts: `types` (XSD schema of the messages), `message`, `portType`/`interface` (the operations), `binding` (protocol + SOAP style — document/literal vs rpc/encoded), and `service`/`port` (the concrete **endpoint** address). It's the contract-first artifact both sides generate code from.

**"What is an endpoint in SOAP?"** — the concrete network address (URL) where the service listens, bound to a specific binding. In the WSDL it's `<service><port binding="..."><soap:address location="https://host/svc"/>`. In JAX-WS, `@WebServiceProvider`/`Endpoint.publish()` on the server side; `BindingProvider.ENDPOINT_ADDRESS_PROPERTY` to override it client-side per environment.

**"What is `wsimport`?"** — the JDK tool that reads a WSDL and **generates Java client stubs** (service class, port interface, JAXB-annotated request/response types). Contract-first codegen.
> **Say this and you stand out:** `wsimport` shipped with the JDK only up to **Java 10**. JAX-WS was removed from the JDK in **Java 11** (JEP 320), so on a modern project you add the standalone `jakarta.xml.ws` / `com.sun.xml.ws` artifacts and use the `jaxws-maven-plugin` (`wsimport` goal) to generate sources at build time instead.

**"How do you consume a SOAP service on the client, and what's the first step?"**
1. **First step: obtain the WSDL** (URL or file) — the contract is the entry point to everything.
2. Generate stubs — `jaxws-maven-plugin:wsimport` (or `wsimport` on old JDKs), bound into `generate-sources`.
3. Instantiate the generated `Service`, get the port: `new MyService().getMyServicePort()`.
4. Point it at the right environment and set timeouts via `((BindingProvider) port).getRequestContext()`.
5. Call the operation like a normal Java method; handle the generated `SOAPFaultException`/checked fault classes.
6. Add handlers (`SOAPHandler`) for WS-Security headers / logging if needed.

**SOAP vs REST**, in case they pivot: SOAP is a **protocol** — XML-only, envelope-based, transport-agnostic, with a rigid WSDL contract and WS-* standards (WS-Security, WS-AtomicTransaction), plus built-in retry/reliability semantics. REST is an **architectural style** — resource URIs, HTTP verbs, any media type (usually JSON), lighter, cacheable, no built-in contract (OpenAPI is bolted on). Pick SOAP when you need formal contracts, message-level security, or you're integrating with an existing enterprise/partner system — which in insurance is often.

---

## 7. 🎯 REST (full treatment on Day 2)

**"How does a REST URL find which method to call?"**

```
request → DispatcherServlet (front controller)
        → HandlerMapping (RequestMappingHandlerMapping)
             matches on: path + HTTP method + params + headers + consumes + produces
        → HandlerAdapter (RequestMappingHandlerAdapter)
        → ArgumentResolvers (@PathVariable, @RequestParam, @RequestBody…)
        → YOUR controller method
        → ReturnValueHandlers → HttpMessageConverter (Jackson → JSON)
        → HttpServletResponse
```
At startup, Spring scans every `@RequestMapping`/`@GetMapping` and builds a `RequestMappingInfo` registry; at request time it picks the **most specific** match and returns 404/405/406 when nothing fits.

**"Write a service for REST"** — they mean an actual end-to-end resource. Rehearse this on Day 2 until you can type it in 45 minutes: entity → repository → service interface + impl → DTO + mapper → `@RestController` with all verbs → bean validation (`@Valid`) → `@ControllerAdvice` global exception handler → correct status codes (201 + `Location` on create, 204 on delete) → pagination → 2 slice tests (`@WebMvcTest`, `@DataJpaTest`).
