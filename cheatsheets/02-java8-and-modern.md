# Java 8+ — Streams, Lambdas, Optional

For a 3–5 year Java role this is **guaranteed** to come up, and their reported list includes "Features of Java 7?", which means version questions are live. Allstate also asks for code, so know the *syntax*, not just the concepts.

---

## Functional interfaces — the four you must name instantly

| Interface | Signature | Used by |
|---|---|---|
| `Function<T,R>` | `R apply(T)` | `map` |
| `Predicate<T>` | `boolean test(T)` | `filter` |
| `Consumer<T>` | `void accept(T)` | `forEach` |
| `Supplier<T>` | `T get()` | `orElseGet`, lazy init |

Plus `BiFunction<T,U,R>`, `UnaryOperator<T>` (`Function<T,T>`), `BinaryOperator<T>` (`reduce`).

A **functional interface** has exactly one abstract method (`@FunctionalInterface` enforces it at compile time) — that's what makes a lambda assignable to it. `default` and `static` methods don't count against the one.

**Method references**, four kinds: `String::length` (instance method of a type), `System.out::println` (bound instance), `String::new` (constructor), `Integer::parseInt` (static).

---

## Streams — the shapes you'll actually be asked to write

```java
// group + count  (the single most-asked stream question)
Map<String, Long> byDept = employees.stream()
    .collect(Collectors.groupingBy(Employee::getDept, Collectors.counting()));

// filter → map → collect
List<String> names = employees.stream()
    .filter(e -> e.getSalary() > 50_000)
    .map(Employee::getName)
    .sorted()
    .toList();                                    // Java 16+; else .collect(toList())

// sum / average / stats
double total = employees.stream().mapToDouble(Employee::getSalary).sum();
IntSummaryStatistics st = employees.stream().mapToInt(Employee::getAge).summaryStatistics();

// max by a field
Optional<Employee> top = employees.stream()
    .max(Comparator.comparingDouble(Employee::getSalary));

// multi-key sort
employees.sort(Comparator.comparing(Employee::getDept)
                         .thenComparing(Employee::getSalary, Comparator.reverseOrder()));

// flatten
List<String> allSkills = employees.stream()
    .flatMap(e -> e.getSkills().stream())
    .distinct()
    .toList();

// partition (boolean split)
Map<Boolean, List<Employee>> split = employees.stream()
    .collect(Collectors.partitioningBy(e -> e.getSalary() > 50_000));

// to map, with a merge function to survive duplicate keys
Map<String, Double> salaryByName = employees.stream()
    .collect(Collectors.toMap(Employee::getName, Employee::getSalary, (a, b) -> a));

// character frequency — a classic Allstate-style string problem, stream version
Map<Character, Long> freq = s.chars()
    .mapToObj(c -> (char) c)
    .collect(Collectors.groupingBy(c -> c, LinkedHashMap::new, Collectors.counting()));

// second highest salary
employees.stream().map(Employee::getSalary).distinct()
    .sorted(Comparator.reverseOrder()).skip(1).findFirst();
```

### Stream facts they probe

- **Lazy.** Intermediate ops (`filter`, `map`, `sorted`) build a pipeline and do nothing. Only a **terminal** op (`collect`, `forEach`, `reduce`, `count`, `findFirst`, `anyMatch`) runs it.
- **Single use.** Reusing a consumed stream throws `IllegalStateException`.
- **`map` vs `flatMap`** — 1→1 vs 1→many-then-flatten.
- **`findFirst` vs `findAny`** — deterministic vs free to pick (matters only in parallel).
- **`peek`** is for debugging, not side effects; it may be skipped entirely by optimisation.
- **Parallel streams** use the **common ForkJoinPool** (`n-1` threads). Only worth it for large data + CPU-bound work + a splittable source. Never use them for blocking I/O — you'll starve the pool that the whole JVM shares. Say this; it's a real-experience marker.
- **`reduce`** needs an identity, an accumulator, and — in parallel — an **associative** combiner.

---

## `Optional` — and how to use it without embarrassing yourself

```java
Optional<User> u = repo.findByEmail(email);

u.map(User::getName).orElse("unknown");
u.orElseGet(() -> createDefault());          // lazy — use for expensive fallbacks
u.orElseThrow(() -> new NotFoundException(email));
u.ifPresentOrElse(this::send, this::logMissing);
u.filter(User::isActive).map(User::getId);
```

**Rules that get tested:**
- **Never** call `.get()` without `isPresent()` — that's just an NPE with extra steps.
- **Never** use `Optional` as a **field**, a **method parameter**, or in a **DTO** — it isn't `Serializable` and it adds a wrapper. It's designed as a **return type** for "might legitimately be absent."
- `orElse` evaluates its argument **eagerly**, `orElseGet` lazily. Using `orElse(expensiveCall())` is a real performance bug.
- Don't return `Optional<List<T>>` — return an empty list.

---

## `java.time` (never use `Date`/`Calendar` in an interview)

`LocalDate`, `LocalTime`, `LocalDateTime` (no zone), `ZonedDateTime`, `Instant` (machine timestamp, UTC), `Duration` (time-based), `Period` (date-based), `DateTimeFormatter`.

All **immutable and thread-safe** — which is precisely why they replaced `SimpleDateFormat`, whose lack of thread safety is a famous production bug (a shared static `SimpleDateFormat` corrupts under concurrency). That's a good story to have ready.

---

## Other Java 8 items

- **`default` methods** — let interfaces evolve without breaking implementors (that's how `Collection.stream()` was added). **Diamond problem:** if two interfaces supply the same default, the class **must** override and can disambiguate with `InterfaceA.super.method()`.
- **`static` interface methods** — utility factories that belong to the interface.
- **`CompletableFuture`** — `supplyAsync`, `thenApply`/`thenCompose` (flatMap-ish), `thenCombine`, `allOf`, `exceptionally`, `handle`. Always pass your **own executor**; the default common pool is shared JVM-wide.
- **PermGen → Metaspace** — class metadata moved to native memory, so `OutOfMemoryError: PermGen space` became `Metaspace`, bounded by `-XX:MaxMetaspaceSize` instead of a fixed default.

---

## Records, sealed, text blocks, pattern matching (Java 17 LTS)

```java
// record — immutable data carrier: final fields, ctor, accessors, equals/hashCode/toString
public record Money(BigDecimal amount, String currency) {
    public Money {                                   // compact constructor: validate here
        if (amount.signum() < 0) throw new IllegalArgumentException("negative");
    }
}

// pattern matching for instanceof — no cast needed
if (o instanceof String s && s.length() > 3) { use(s); }

// switch expression + text block
String desc = switch (status) {
    case ACTIVE, TRIAL -> "live";
    case CLOSED -> "done";
};
String json = """
    { "id": 1 }""";

// sealed — a closed hierarchy, so switch can be exhaustive
public sealed interface Shape permits Circle, Square {}
```

**Where records shine:** DTOs and value objects. They also make the deep-copy question disappear — an immutable record needs no copying.

---

## Virtual threads (Java 21) — mention only if asked

Lightweight threads scheduled by the JVM onto a small pool of carrier threads. They make the **thread-per-request** model viable again for **I/O-bound** work, because a blocking call parks the virtual thread instead of an OS thread. `Executors.newVirtualThreadPerTaskExecutor()`. They don't help CPU-bound work, and `synchronized` blocks used to pin the carrier thread. Knowing the *boundary* of where they help is the impressive part, not knowing they exist.
