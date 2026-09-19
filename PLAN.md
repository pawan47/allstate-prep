# Allstate India — 7-Day Interview Plan
**Target role:** Java / Spring Boot Developer, mid-level (3–5 yrs)
**Built:** Sat 19 Sep 2026 · interview ~Fri 26 Sep

---

## What you are actually walking into

This is the reported Allstate India pipeline. Prepare for *this*, not for a generic FAANG loop.

| # | Round | Length | What is actually tested |
|---|-------|--------|--------------------------|
| 1 | **L1 Technical** | **~2 hrs** | Core Java, Spring Boot, Microservices, Kafka, AWS, Azure DevOps CI/CD, Spring REST APIs. **You write code**: Java programs + an end-to-end REST API with "all the key features." |
| 2 | **Coding Test** | ~1 hr | Arrays & Strings dominate. LRU Cache, longest substring, rotated-array search, merge intervals. They probe **time complexity and edge cases** explicitly. |
| 3 | **L2 Technical** | ~1 hr | Your projects, deep-dive on your own architecture, **plus a puzzle** to test thinking-on-your-feet. |
| 4 | **L2 Repeat** | ~1 hr | **Kafka in depth**: topics, partitions, consumer groups, Debezium connector, KStreams, error handling, Avro schemas, acks, commits, replicas. |
| 5 | **Managerial** | ~45 min | Real-time project questions, design patterns you used, architecture decisions. Conversational. |
| 6 | **HR** | ~30 min | STAR-format behavioural, values fit, comp. |

Overall pipeline ≈ 4 weeks. Reported difficulty **2.6/5** and **79% positive** — this is a *fundamentals* loop, not a puzzle gauntlet. **Depth on basics beats breadth on exotica.**

### Three culture facts that change how you answer

1. **Allstate is an XP shop.** Pair programming, TDD, and CI/CD are not buzzwords there — they are the daily process. Every answer you give should sound like someone who writes tests first.
2. **They screen for mechanism, not definitions.** Reported verbatim: interviewers want *"precise, to-the-point answers that demonstrate clear understanding of internal framework mechanisms rather than surface-level definitions."* "HashMap stores key-value pairs" fails. "Bucket array, hash spread by `h ^ (h>>>16)`, treeifies at 8" passes.
3. **12-Factor app principles** are named in their engineering standards. Know the 12 headings well enough to name the 4 that matter for your project (config, logs, processes, disposability).

---

## The 7 days

### Day 1 — Sat 19 Sep · **CORE JAVA** ← you are here
Full hour-by-hour breakdown in **[DAY1-JAVA.md](DAY1-JAVA.md)**. Headline: build `MySet` and `MyHashMap` by hand with TDD, then run the four demos so collections and concurrency answers come from experience, not memory.

### Day 2 — Sun 20 Sep · **Spring Boot internals + REST API end-to-end**
- Build one complete REST resource from scratch, timed at 45 min: entity → repo → service → controller → DTO+mapper → validation → `@ControllerAdvice` exception handling → 2 slice tests. They ask for exactly this in L1.
- Know cold: IoC/DI, bean scopes, bean lifecycle, `@Component` vs `@Bean`, autowiring modes and the `@Qualifier`/`@Primary` tiebreak, `@Transactional` propagation + why it silently no-ops on self-invocation and on private methods, auto-configuration (`@EnableAutoConfiguration` → `spring.factories`/`AutoConfiguration.imports`), starter dependencies, embedded server, actuator, profiles, `@ConfigurationProperties` vs `@Value`.
- **The reported question**: *"How does a REST URL find which method to call?"* → `DispatcherServlet` → `HandlerMapping` (`RequestMappingHandlerMapping`) → `HandlerAdapter` → your method → `HttpMessageConverter` → response. Draw it.
- Also reported: **SOAP legacy** — WSDL, endpoint, `wsimport`, how to consume a service client-side. Allstate is an insurance company with real SOAP services. 20 minutes here is cheap insurance.

### Day 3 — Mon 21 Sep · **Microservices + design patterns**
- Decomposing a monolith: bounded contexts, database-per-service, the shared-DB anti-pattern.
- Communication: REST vs gRPC vs Kafka, sync vs async, and *when* each.
- Resilience: **Circuit Breaker** (Resilience4j states + thresholds), retry with backoff+jitter, bulkhead, timeout, fallback.
- Data patterns: **Saga** (orchestration vs choreography), **CQRS**, **Outbox**, idempotency keys, distributed tracing.
- Patterns you must be able to name *from your own code*: Builder, Factory, Strategy, Singleton, Template Method, Proxy (and that Spring AOP *is* Proxy), Observer. The managerial round asks "which patterns did you use?" — have 3 concrete answers with the file they live in.

### Day 4 — Tue 22 Sep · **Kafka deep-dive** (this is round 4; it is heavily weighted)
- Topics, partitions, offsets, consumer groups, rebalancing, partition assignment strategies.
- Producer: `acks=0/1/all`, `min.insync.replicas`, idempotent producer, `enable.idempotence`, retries, batching/linger.
- Consumer: auto vs manual commit, `commitSync` vs `commitAsync`, at-least-once vs exactly-once, poll loop and `max.poll.interval.ms`.
- Replication: leader/follower, ISR, unclean leader election.
- **Avro + Schema Registry**: why schemas, backward vs forward compatibility.
- **Debezium** CDC connector (named explicitly in a reported round), Kafka Connect.
- **Kafka Streams**: KStream vs KTable, stateful ops, windowing.
- Error handling: retry topics, dead-letter topic, `DefaultErrorHandler`, poison-pill messages.
- Spring: `@KafkaListener`, `KafkaTemplate`, `ConcurrentKafkaListenerContainerFactory`.

### Day 5 — Wed 23 Sep · **DSA coding-test simulation**
Timed, out loud, no IDE autocomplete. Use the dojo. Order: rotated-array search → longest substring → merge intervals → LRU → max-subarray-sum-k → string compression. Then SQL joins/group-by/window basics, and JPA `@Query` + N+1 problem.

### Day 6 — Thu 24 Sep · **Projects, STAR stories, system design of your own work**
- Draw your current architecture on one page. Every box: why it exists, what you'd change, what broke once.
- 6 STAR stories: conflict, failure, tight deadline, disagreed with a decision, mentored someone, production incident you fixed.
- Prepare your **questions for them** — about XP practice, pair rotation, on-call, how they do CI/CD.
- AWS + Azure DevOps CI/CD: name the services you've used and the pipeline stages.

### Day 7 — Fri 25 Sep · **Consolidate, don't cram**
Re-run the whole dojo from empty. Re-read the cheatsheets. Two full mock rounds out loud. Sleep early. No new topics — new material the night before only shakes confidence.

---

## Non-negotiables

- **Say complexity before you code.** Reported: they push on time complexity. Lead with "this is O(n log n) because of the sort."
- **Talk continuously.** In an XP shop, silence during code reads as inability to pair. Narrate.
- **Write the test first**, even in the plain coding round. It is free signal that you match their culture.
- **Ask about edge cases unprompted**: null, empty, single element, duplicates, overflow, capacity 0.
- **"I don't know, but here's how I'd find out"** beats a confident wrong answer, every time.
