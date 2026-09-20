# Core Java study guide — everything else they ask

[01-core-java-qbank.md](01-core-java-qbank.md) covers the questions reported **verbatim**
from Allstate. This file covers the rest of what their L1 round is described as testing:

> *"All concepts of Core Java"* … candidates should be able to *"comfortably explain core
> Java concepts **down to the memory level**, including garbage collection, collection
> internals, and thread synchronization mechanisms"* … plus *"basic OOP concepts and data
> structures, some database topics such as SQL, BCNF."*

Study as sections. Tick a box only when you can say the answer **out loud, unprompted**.

---

## Section 1 — OOP fundamentals (explicitly reported)

- [ ] **The four pillars**, each with an example *from your own project*, not a textbook animal:
  - **Encapsulation** — private state + public behaviour; why getters/setters aren't automatically encapsulation
  - **Inheritance** — `extends`, and why "favour composition over inheritance" is the standard advice
  - **Polymorphism** — compile-time (overloading) vs runtime (overriding/dynamic dispatch)
  - **Abstraction** — hiding *how*, exposing *what*
- [ ] **Abstract class vs interface** — state + constructor + any access modifier vs multiple inheritance of type. Since Java 8 interfaces have `default`/`static`; since 9, `private`. Choose interface for a *capability*, abstract class for *shared state*.
- [ ] **Overloading vs overriding rules** — overriding: same signature, covariant return, access **not narrower**, **no broader checked exceptions**. `static`/`private`/`final` methods are **hidden, not overridden**.
- [ ] **Why no multiple inheritance of classes** — the diamond problem. Interfaces sidestep it because they had no state; with `default` methods the collision returns, and the class **must** override, disambiguating via `InterfaceA.super.method()`.
- [ ] **`super` vs `this`**, constructor chaining, and why `super()` must be the first statement
- [ ] **Composition vs inheritance** — "is-a" vs "has-a"; inheritance couples you to the parent's implementation, which is why `Vector`/`Stack` are cautionary tales
- [ ] **`static`** — belongs to the class; static block runs once at class-load; a static method can't be overridden and can't see `this`
- [ ] **`final`** — final variable (assign once), final method (can't override), final class (can't extend, e.g. `String`)
- [ ] **Immutability** — how to build an immutable class: `final` class, `private final` fields, no setters, **defensive copies of mutable fields in both the constructor and the getters**. That last clause is what separates a real answer.

### The SOLID principles — name all five with a one-line example
- [ ] **S**ingle Responsibility · **O**pen/Closed · **L**iskov Substitution · **I**nterface Segregation · **D**ependency Inversion
- [ ] **Why DI matters to Spring** — Dependency Inversion is the *principle*; Spring's IoC container is the *mechanism*. Say it that way and the Spring questions open up.

---

## Section 2 — Design patterns (the managerial round asks for yours)

They ask *"which design patterns did you use in your project?"* Have **three real answers**
with the class they live in. Vague answers fail this.

- [ ] **Singleton** — and everything wrong with it: not thread-safe naively; double-checked locking **requires `volatile`**; reflection and deserialization both bypass the private constructor; **`enum` is the correct singleton** in Java. Spring beans are singleton-scoped *per container*, which is not the same as the GoF pattern.
- [ ] **Factory / Abstract Factory** — creation behind an interface; where you've used it
- [ ] **Builder** — many optional params, immutable result. Why it beats a 7-arg constructor or telescoping overloads.
- [ ] **Strategy** — swap an algorithm at runtime; in modern Java it's often just a lambda
- [ ] **Template Method** — skeleton in the base class, steps overridden. `JdbcTemplate` is the name-drop.
- [ ] **Proxy / Decorator** — **Spring AOP *is* dynamic proxying**; this is why `@Transactional` silently does nothing on self-invocation or a private method
- [ ] **Observer** — event listeners; `ApplicationEventPublisher` in Spring
- [ ] **Adapter**, **Facade** — one line each
- [ ] **DAO / Repository** — you use this daily; be able to name it as a pattern

---

## Section 3 — Exception handling

- [ ] **Hierarchy** — `Throwable` → `Error` (don't catch: `OutOfMemoryError`, `StackOverflowError`) and `Exception` → `RuntimeException`
- [ ] **Checked vs unchecked** — recoverable and part of the contract vs programming error. Design guidance: don't use checked exceptions for things callers can't fix.
- [ ] **`throw` vs `throws`**, and multi-catch `catch (A | B e)`
- [ ] **`try-with-resources`** — needs `AutoCloseable`; resources close in **reverse** order; exceptions from `close()` become **suppressed** exceptions rather than masking the original. This replaced the buggy finally-block idiom.
- [ ] **Does `finally` always run?** Almost. Not on `System.exit()`, a JVM crash, or an infinite loop/daemon-thread kill. **A `return` in `finally` silently discards a pending exception or return value** — a classic gotcha.
- [ ] **Custom exceptions** — when to extend `RuntimeException` vs `Exception`; always keep the `cause` constructor so you don't lose the stack trace
- [ ] **Anti-patterns to name**: empty catch block; `catch (Exception e)` swallowing everything; catching `Throwable`; using exceptions for control flow; logging *and* rethrowing (double-logging)
- [ ] **`throw` inside a `finally`** loses the original exception

---

## Section 4 — JVM, memory, garbage collection ⭐ (explicitly reported as "down to the memory level")

- [ ] **JVM vs JRE vs JDK** — runtime engine / runtime + libraries / JRE + compiler & tools
- [ ] **Compilation path** — `.java` → `javac` → bytecode `.class` → JVM interprets, then **JIT** compiles hot methods to native. "Write once, run anywhere" comes from bytecode.
- [ ] **Class loading** — Bootstrap → Platform/Extension → Application, with **delegation upward**; load → link (verify, prepare, resolve) → initialise
- [ ] **Memory areas** — know which is which:
  - **Heap** — all objects, split **Young** (Eden + 2 Survivor spaces) and **Old/Tenured**
  - **Stack** — per thread: frames, locals, references. `StackOverflowError` lives here.
  - **Metaspace** — class metadata; **native memory** since Java 8, replacing PermGen
  - **Code cache** — JIT-compiled native code
- [ ] **Stack vs heap** — primitives and *references* on the stack, *objects* on the heap. Each thread has its own stack; the heap is shared — which is the root of every visibility problem.
- [ ] **`OutOfMemoryError: Java heap space` vs `Metaspace` vs `StackOverflowError`** — and the likely cause of each
- [ ] **How GC decides** — **reachability** from GC roots (stack locals, static fields, JNI refs), *not* reference counting. Mark → sweep → compact.
- [ ] **Generational hypothesis** — most objects die young; so minor GC on Eden is cheap and frequent, major/full GC on Old is expensive
- [ ] **Minor vs major vs full GC**, and **stop-the-world** pauses
- [ ] **Collectors** — Serial, Parallel (throughput), **G1** (default since Java 9, region-based, pause-target), ZGC/Shenandoah (sub-millisecond, very large heaps)
- [ ] **`finalize()`** — deprecated in 9, **removed in 18**. Never rely on it. Use `try-with-resources` or `Cleaner`.
- [ ] **Reference types** — strong, **soft** (cleared under memory pressure → caches), **weak** (cleared at next GC → `WeakHashMap`), phantom
- [ ] **Memory leaks in a GC'd language** — yes, really: static collections that only grow; unremoved listeners; `ThreadLocal` in a thread pool without `remove()`; unclosed resources; mutable keys in a `HashMap`
- [ ] **Can you force GC?** `System.gc()` is a *suggestion*; never depend on it
- [ ] **Diagnostic tools to name** — `jps`, `jstat`, `jmap`, `jstack`, heap dumps + Eclipse MAT, JFR/VisualVM. Naming these signals production experience.

---

## Section 5 — Thread synchronization ⭐ (explicitly reported)

[01-core-java-qbank.md](01-core-java-qbank.md) has `wait`/`sleep` and the concurrent
collections. Add these:

- [ ] **Thread lifecycle** — NEW → RUNNABLE → BLOCKED / WAITING / TIMED_WAITING → TERMINATED
- [ ] **Creating threads** — `extends Thread` vs `implements Runnable` (prefer Runnable: composition, and you keep your one inheritance slot) vs `Callable` + `ExecutorService`
- [ ] **`start()` vs `run()`** — calling `run()` directly runs on the *current* thread, no new thread at all. Classic trick question.
- [ ] **Race condition vs deadlock vs livelock vs starvation** — one line each
- [ ] **`synchronized`** — method vs block; instance lock (`this`) vs class lock (`Class` object); reentrant; gives **both** mutual exclusion and visibility
- [ ] **`volatile`** — visibility + ordering, **no atomicity**. `volatile i++` is still a race. Correct uses: a stop flag, and the double-checked-locking singleton.
- [ ] **Java Memory Model / happens-before** — why an unsynchronised write may never be seen by another thread (CPU caches, compiler reordering). `synchronized`, `volatile`, and `Thread.start`/`join` all establish happens-before edges.
- [ ] **Atomic classes** — `AtomicInteger`, `AtomicReference`; **CAS** and the ABA problem; `LongAdder` for high contention
- [ ] **`ExecutorService`** — the pool types, `submit` vs `execute`, `shutdown` vs `shutdownNow`, why an unbounded `newCachedThreadPool` is dangerous, and why you should size pools deliberately
- [ ] **`Future` vs `CompletableFuture`** — blocking and non-composable vs chainable with error handling
- [ ] **Coordination utilities** — `CountDownLatch` (one-shot), `CyclicBarrier` (reusable), `Semaphore` (permits), `BlockingQueue` (producer/consumer — the *right* answer instead of `wait`/`notify`)
- [ ] **`synchronized` vs `ReentrantLock`** — the Lock adds `tryLock`, timeout, interruptibility, fairness, multiple `Condition`s; you must `unlock()` in a `finally`
- [ ] **`ThreadLocal`** — per-thread state; **leaks in thread pools** unless you `remove()`
- [ ] **Deadlock prevention** — consistent global lock ordering, `tryLock` with timeout; detect with `jstack`
- [ ] **Immutability as a concurrency strategy** — the senior answer: no shared mutable state, no synchronisation needed

---

## Section 6 — Strings, generics, serialization, misc

- [ ] **String pool** — literals are interned; `new String("x") != "x"`; `intern()`; why immutability enables pooling, safe sharing and a cached `hashCode`
- [ ] **`String` vs `StringBuilder` vs `StringBuffer`** — and why `+` in a loop is O(n²)
- [ ] **`==` vs `equals`** for strings and for `Integer` (the **−128..127** cache; `127 == 127` is true, `128 == 128` is false)
- [ ] **`equals`/`hashCode` contract** — equal objects must have equal hashes; override together; what breaks if you don't
- [ ] **Generics** — type safety at compile time; **type erasure** and its consequences: no `new T[]`, no `instanceof T`, can't overload on `List<String>` vs `List<Integer>`
- [ ] **Wildcards / PECS** — `? extends T` to read (Producer Extends), `? super T` to write (Consumer Super)
- [ ] **Serialization** — `Serializable` as a marker; **`serialVersionUID`** and what happens when it mismatches; `transient` skips a field; **deserialization bypasses the constructor**; why Java serialization is considered a security risk and JSON is preferred
- [ ] **`Comparable` vs `Comparator`**, and `Comparator.comparing().thenComparing()`
- [ ] **Autoboxing** — the hidden cost in loops, and `NullPointerException` from unboxing a null `Integer`
- [ ] **varargs**, enhanced for, labelled break — small, occasionally asked
- [ ] **`enum`** — can have fields, constructors and methods; the correct singleton; `EnumMap`/`EnumSet` are faster than the hash versions

---

## Section 7 — SQL & databases (reported: *"some database topics such as SQL, BCNF"*)

- [ ] **Joins** — INNER, LEFT, RIGHT, FULL, CROSS, SELF. Be able to *draw* them.
- [ ] **`WHERE` vs `HAVING`** — row filter before grouping vs group filter after
- [ ] **`GROUP BY` + aggregates** — COUNT/SUM/AVG/MIN/MAX; `COUNT(*)` vs `COUNT(col)` and NULL
- [ ] **Window functions** — `ROW_NUMBER()`, `RANK()`, `DENSE_RANK()` with `OVER (PARTITION BY … ORDER BY …)`; the difference between the three ranks
- [ ] **Classic query: Nth highest salary** — via `DENSE_RANK()` or a correlated subquery. Extremely common.
- [ ] **Find duplicates** — `GROUP BY col HAVING COUNT(*) > 1`
- [ ] **Indexes** — B-tree; clustered vs non-clustered; composite index **left-prefix rule**; why an index slows writes; when a scan beats an index
- [ ] **Normalization ⭐** — they name **BCNF** specifically:
  - **1NF** atomic values, no repeating groups
  - **2NF** 1NF + no partial dependency on part of a composite key
  - **3NF** 2NF + no transitive dependency (non-key → non-key)
  - **BCNF** a stricter 3NF: **every determinant must be a candidate key**. Know one example where a table is in 3NF but *not* BCNF (overlapping candidate keys) — that's the actual question.
  - **Denormalization** — and why you'd deliberately do it
- [ ] **ACID**, isolation levels (READ UNCOMMITTED → SERIALIZABLE) and the anomalies each prevents: dirty read, non-repeatable read, phantom read
- [ ] **Transactions** — commit/rollback/savepoint; optimistic vs pessimistic locking
- [ ] **JPA/Hibernate** — entity lifecycle (transient/persistent/detached/removed); **lazy vs eager**; the **N+1 select problem** and its fixes (`JOIN FETCH`, `@EntityGraph`, batch size); `save` vs `saveAndFlush`; first-level cache; **`@Transactional` propagation**; why `equals`/`hashCode` on entities is subtle
- [ ] **SQL vs NoSQL** — when you'd pick each, in one sentence

---

## Section 8 — Build, CI/CD, cloud (their L1 names these)

- [ ] **Maven** — lifecycle phases, scopes, `dependency:tree`, nearest-definition-wins conflict resolution, `<dependencyManagement>`, profiles
- [ ] **Azure DevOps CI/CD** ⭐ — reported by name. `azure-pipelines.yml`, stages/jobs/steps, service connections, variable groups + Key Vault, hosted vs self-hosted agents, environments with approval gates. **Map your Jenkins experience onto these terms if that's what you've used** — showing the concepts transfer is the answer they want.
- [ ] **Git** — rebase vs merge, cherry-pick, resolving conflicts, your branching strategy
- [ ] **AWS** — reported. Name the services you've actually touched (EC2, S3, RDS, Lambda, SQS/SNS, ECS/EKS, CloudWatch, IAM, Secrets Manager) and say what you used each *for*. Don't claim breadth you don't have.
- [ ] **Docker** — image vs container, layers, a multi-stage build for a Spring Boot jar, `docker-compose`
- [ ] **12-Factor app** ⭐ — named in Allstate's engineering standards. Know all 12 headings well enough to discuss the four that matter most: **config in the environment**, **logs as event streams**, **stateless processes**, **disposability**.

---

## How to use this

1. **Day 1 (today):** Sections 1, 3, 4, 5 — OOP, exceptions, memory/GC, threads. These are the explicitly reported ones.
2. **Day 2:** Section 8 alongside the Spring Boot day.
3. **Day 3:** Section 2 — patterns, while you do microservices.
4. **Day 5:** Section 7 — SQL, with the DSA simulation.
5. **Day 6:** Section 6 as a sweep, then re-read every box you left unticked.

**Don't read this cover to cover.** Pick a section, cover the text, say the answers aloud,
and mark only what you got right. Unticked boxes on Day 7 are your revision list.
