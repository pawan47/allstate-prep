# Spring Boot question bank

🎯 = reported by an Allstate candidate. Their L1 round is described as covering
*"all concepts of Core Java, **Springboot**, Microservices, Apache Kafka, AWS, Azure DevOps
CI/CD and **Spring Native APIs**"*, and requires *"code to be written for Java programs and
**end to end REST API with all the key features**."*

Reminder on how to answer: they screen for **internal mechanism**, not definitions. "Spring
Boot reduces boilerplate" is a fail. "Auto-configuration classes are listed in
`AutoConfiguration.imports` and applied conditionally via `@ConditionalOnClass` /
`@ConditionalOnMissingBean`" is a pass.

---

## 1. Core Spring — IoC and DI

### "What is IoC / Dependency Injection?"

**Inversion of Control** — you don't construct your collaborators; the container does, and
hands them to you. **Dependency Injection** is how IoC is implemented.

Why it matters (say this, it's the real answer): your class depends on an *interface* it
doesn't instantiate, so in a test you inject a stub instead. **DI exists to make code
testable and swappable** — which for an XP/TDD shop like Allstate is exactly the point.

Note the link to SOLID: **Dependency Inversion** is the principle; the IoC container is the
mechanism.

### "Which injection type, and why?"

| Type | Verdict |
|---|---|
| **Constructor** ✅ | **Use this.** Dependencies are `final` and mandatory, the object is never half-built, and it's trivially testable with `new`. Since Spring 4.3 `@Autowired` is optional on a single constructor. |
| Setter | Only for genuinely optional dependencies |
| Field (`@Autowired` on a field) | ❌ Avoid: hides dependencies, can't be `final`, needs reflection or a running container to test |

**Killer follow-up: "how do you handle a circular dependency?"** Field injection lets one
compile and blow up at runtime; **constructor injection fails fast at startup**, which is
better. The real fix is to redesign — extract the shared logic into a third bean.
`@Lazy` is a band-aid. Since Boot 2.6 circular references are **rejected by default**.

### "`@Component` vs `@Bean`?"
- `@Component` (+ `@Service`, `@Repository`, `@Controller`) — on **your own** class, found by classpath scanning
- `@Bean` — a **method** in a `@Configuration` class; use it for third-party classes you can't annotate, or when construction needs logic
- `@Service`/`@Repository` are semantic markers; **`@Repository` additionally translates persistence exceptions** into Spring's `DataAccessException` hierarchy. Knowing that specific difference scores.

### "Two beans of the same type — how does Spring choose?"
Resolution order: **by type** → if several, **`@Primary`** → **`@Qualifier("name")`** →
match the field/parameter name. Otherwise `NoUniqueBeanDefinitionException`. For a list of
all of them, inject `List<MyInterface>` and order with `@Order`.

### "Bean scopes?"
`singleton` (default, **one per container** — not the GoF singleton, and not one per JVM),
`prototype` (new each request; note Spring does **not** manage its full lifecycle or call
`@PreDestroy`), plus the web scopes `request`, `session`, `application`, `websocket`.

**Trap they like:** injecting a `prototype` bean into a `singleton` gives you **one**
instance forever, because injection happens once at startup. Fix with `ObjectProvider`,
`@Lookup`, or a scoped proxy.

### "Bean lifecycle?"
instantiate → populate properties → `BeanNameAware`/`BeanFactoryAware` →
`BeanPostProcessor.postProcessBeforeInitialization` → `@PostConstruct` →
`InitializingBean.afterPropertiesSet` → custom `initMethod` →
`postProcessAfterInitialization` → **ready** → `@PreDestroy` → `DisposableBean.destroy`.

You don't need it verbatim; you *do* need `@PostConstruct` / `@PreDestroy` and that
`BeanPostProcessor` is the hook AOP proxies are created in.

---

## 2. Spring Boot specifics

### "What does Spring Boot actually add over Spring?"
Auto-configuration, starter POMs, an embedded server, externalised config with profiles,
Actuator, and opinionated defaults. **It adds no new DI capability** — it's Spring plus
convention. Saying that shows you know where the line is.

### 🎯 "What is `@SpringBootApplication`?"
Three annotations in one:
- **`@SpringBootConfiguration`** — a `@Configuration` class, the primary source
- **`@ComponentScan`** — scans **this package and below** (which is why placement matters)
- **`@EnableAutoConfiguration`** — triggers auto-config

### "How does auto-configuration work, mechanically?" ⭐
1. `@EnableAutoConfiguration` imports `AutoConfigurationImportSelector`.
2. It reads candidate class names from every jar's
   `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports`
   (Boot 2.7+; previously `spring.factories`).
3. Each candidate is applied only if its **`@Conditional`** annotations hold:
   `@ConditionalOnClass`, `@ConditionalOnMissingBean`, `@ConditionalOnProperty`,
   `@ConditionalOnWebApplication`.
4. **`@ConditionalOnMissingBean` is why your own bean silently wins** — define a `DataSource`
   and Boot's backs off.

**Debug it with `--debug`** or `/actuator/conditions`: it prints the positive and negative
matches. Mentioning that you've read that report is a strong experience signal.

### "How do you disable one auto-configuration?"
`@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)` or
`spring.autoconfigure.exclude` in properties.

### "What's in a starter?"
No code — just a curated, version-aligned set of transitive dependencies.
`spring-boot-starter-web` brings Spring MVC, Jackson, validation and embedded Tomcat.
`spring-boot-starter-parent` centralises versions so you omit `<version>` tags.

### "`@Value` vs `@ConfigurationProperties`?"
`@Value("${x}")` for a single value with SpEL support. **`@ConfigurationProperties`** for a
typed, validated group — supports nesting, relaxed binding, `@Validated`, and IDE
completion. Prefer it for anything beyond one property.

### "Profiles?"
`application-dev.yml` / `application-prod.yml`, activated by
`spring.profiles.active`. `@Profile("dev")` on a bean. Property precedence (highest first):
command line → `SPRING_APPLICATION_JSON` → OS env vars → profile-specific files → the base
`application.yml` → `@PropertySource` → defaults. **Env vars overriding files is the
12-Factor "config in the environment" rule** — worth linking explicitly.

### "Spring Boot Actuator?"
Production endpoints: `/actuator/health` (with readiness/liveness probes for Kubernetes),
`/info`, `/metrics` (via Micrometer → Prometheus), `/env`, `/loggers` (change log level at
runtime, no redeploy), `/threaddump`, `/httpexchanges`, `/conditions`, `/beans`.
**Security point to volunteer:** only `health` and `info` are exposed over HTTP by default;
anything more must be deliberately exposed and secured.

### 🎯 "Spring Native APIs" (named in their L1)
Two plausible readings — cover both briefly:
- **Spring's own REST APIs** — i.e. plain `@RestController`/`RestTemplate`/`WebClient` rather than a third-party stack
- **Spring Native / GraalVM AOT** — ahead-of-time compilation to a native image: millisecond startup, far lower memory, at the cost of build time and **no runtime reflection unless registered**. Boot 3 has first-class support via `spring-boot-starter-parent` + the GraalVM plugin (`mvn -Pnative native:compile`). Relevant because it suits serverless and fast-scaling containers.

---

## 3. REST with Spring MVC

### 🎯 "How does a REST URL find which method to call?" — *asked verbatim*

```
HTTP request
  → DispatcherServlet                    (the front controller)
  → HandlerMapping                       (RequestMappingHandlerMapping)
        matches on: path + HTTP method + params + headers + consumes + produces
  → HandlerAdapter                       (RequestMappingHandlerAdapter)
  → HandlerInterceptors (preHandle)
  → ArgumentResolvers                    (@PathVariable, @RequestParam, @RequestBody…)
  → YOUR CONTROLLER METHOD
  → ReturnValueHandlers
  → HttpMessageConverter                 (Jackson: object → JSON)
  → HttpServletResponse
```

At startup Spring scans every `@RequestMapping` and builds a `RequestMappingInfo` registry.
At request time it picks the **most specific** match, and returns **404** (no path), **405**
(path but wrong method), **415** (bad `Content-Type`) or **406** (can't satisfy `Accept`).

### 🎯 "Write a service for REST" — they want a full resource
See [DAY2-SPRINGBOOT.md](../DAY2-SPRINGBOOT.md) for the timed drill. "All the key features"
means: entity → repository → service → **DTO + mapper** → controller with all verbs →
**bean validation** → **`@ControllerAdvice`** → correct status codes → **pagination** →
tests.

### Annotation quick-fire
- `@RestController` = `@Controller` + `@ResponseBody`
- `@RequestMapping` vs `@GetMapping`/`@PostMapping`/`@PutMapping`/`@PatchMapping`/`@DeleteMapping`
- `@PathVariable` (in the path) vs `@RequestParam` (query string) vs `@RequestBody` (deserialised body)
- `@ResponseStatus`, or return `ResponseEntity` for full control of status + headers
- `@Valid` / `@Validated` to trigger bean validation

### "PUT vs PATCH vs POST?"
POST creates (not idempotent), **PUT replaces the whole resource (idempotent)**, PATCH
applies a partial update. Idempotency is the answer they're listening for.

### "Which status codes?"
200 OK · **201 Created + a `Location` header** · 202 Accepted · **204 No Content** (delete) ·
400 Bad Request · 401 vs **403** (unauthenticated vs unauthorised) · 404 · 409 Conflict ·
422 · 429 · 500 · 503. Getting **201 + Location** and **204 on delete** right is a small
detail that marks an experienced API author.

### "Why DTOs instead of returning entities?"
Decouples the API contract from the schema; avoids leaking fields; prevents Jackson
lazy-loading blowups (`LazyInitializationException` / infinite recursion on bidirectional
relationships); lets the API version independently. Map with MapStruct or by hand.

### "Global exception handling?"
`@RestControllerAdvice` + `@ExceptionHandler(MyNotFoundException.class)` returning a
consistent error body. Extend `ResponseEntityExceptionHandler` to reshape Spring's own
errors. Modern answer: **RFC 7807 `ProblemDetail`** (built into Spring 6 / Boot 3).

### "How do you version an API?"
URI path (`/v1/…`), a header, or content negotiation. Path versioning is the pragmatic
default; say the trade-off rather than claiming one is correct.

---

## 4. Data & transactions

### "`@Transactional` — how does it actually work?" ⭐
Spring creates a **proxy** around your bean; the proxy opens a transaction before the method
and commits or rolls back after. **Because it's a proxy:**

- ❌ **Self-invocation does nothing.** `this.otherTransactionalMethod()` bypasses the proxy entirely.
- ❌ **`private`, `static` and `final` methods** can't be advised.
- ⚠️ **Default rollback is only on unchecked exceptions.** A checked exception **commits** unless you set `rollbackFor`.

Those three bullets are the most common real-world Spring bug and a favourite question.

- [ ] **Propagation** — `REQUIRED` (default), `REQUIRES_NEW`, `NESTED`, `SUPPORTS`, `MANDATORY`, `NEVER`, `NOT_SUPPORTED`
- [ ] **Isolation** — maps to the DB levels; know the anomaly each prevents
- [ ] `readOnly = true` as an optimisation hint

### Spring Data JPA
- `JpaRepository` gives CRUD + paging for free; **derived query methods** (`findByLastNameAndActiveTrue`); `@Query` for JPQL or native SQL; `@Modifying` for writes
- **N+1 problem** ⭐ — fetching 100 orders then lazily touching each customer = 101 queries. Fix with `JOIN FETCH`, `@EntityGraph`, or `@BatchSize`. **Be ready to explain how you detected it** (SQL logging / `spring.jpa.show-sql`) — that's the experience question.
- **Lazy vs eager**, and `LazyInitializationException` outside a transaction
- Entity states: transient → persistent → detached → removed
- `save()` on a detached entity does a merge; **`saveAll` isn't automatically batched** without `hibernate.jdbc.batch_size`
- Optimistic locking with **`@Version`**
- Flyway/Liquibase for migrations; **never `ddl-auto=update` in production**

---

## 5. Testing (an XP shop will push here) ⭐

This section matters more at Allstate than at most companies. They practise TDD and pair
programming daily — they will ask what and how you test.

- [ ] **`@SpringBootTest`** loads the whole context — slow; use sparingly
- [ ] **Slice tests** — `@WebMvcTest` (controllers + `MockMvc`, no DB), `@DataJpaTest` (repositories + in-memory DB + rollback per test), `@JsonTest`, `@RestClientTest`
- [ ] **`@MockBean`** replaces a bean in the context; plain **Mockito `@Mock` + `@InjectMocks`** needs no context and is much faster — prefer it for unit tests
- [ ] **`MockMvc`** — `mockMvc.perform(get("/api/x")).andExpect(status().isOk()).andExpect(jsonPath("$.name").value("x"))`
- [ ] **Testcontainers** — a real Postgres in Docker for integration tests; the modern answer to "how do you test against the real DB?"
- [ ] **`@Transactional` on a test** rolls back automatically
- [ ] **Test pyramid** — many unit, some integration, few end-to-end
- [ ] **Be ready for: "do you write tests first?"** Have an honest, concrete answer. If you haven't done strict TDD, say what you *do* do and that you're comfortable pairing in a TDD flow — don't bluff a practice you can't demonstrate in the next round, because they *will* pair with you.

---

## 6. Microservices, Kafka, resilience (Day 3 and Day 4)

Headlines only here; the depth is in [PLAN.md](../PLAN.md) Days 3–4.

- **Decomposition** — bounded contexts, **database per service**, why a shared DB is an anti-pattern
- **Communication** — REST (simple, sync, coupled in time) vs gRPC (binary, contract-first, fast) vs **Kafka** (async, decoupled, replayable)
- **Service discovery** — Eureka / Kubernetes DNS; **API gateway** — Spring Cloud Gateway
- **Config** — Spring Cloud Config, or ConfigMaps/Secrets
- **Resilience4j** — circuit breaker (CLOSED → OPEN → HALF_OPEN), retry with backoff **and jitter**, bulkhead, rate limiter, timeout, fallback
- **Distributed transactions** — **Saga** (orchestration vs choreography), **Outbox** pattern, **idempotency keys**, **CQRS**
- **Observability** — correlation IDs, Micrometer Tracing, structured logs, the three pillars
- **Kafka** ⭐ (their L2-repeat round) — topics/partitions/consumer groups/rebalancing; `acks` and `min.insync.replicas`; idempotent producer; manual vs auto commit; at-least-once vs exactly-once; ISR; **Avro + Schema Registry** and compatibility modes; **Debezium** CDC; **Kafka Streams** KStream vs KTable; retry and **dead-letter topics**; `@KafkaListener` and `KafkaTemplate`
- **12-Factor** — all twelve headings; be fluent on config, logs, stateless processes, disposability

---

## The five you're most likely to be asked

If you only have an hour, own these:

1. **How auto-configuration works** — `AutoConfiguration.imports` + `@Conditional*` + `@ConditionalOnMissingBean` backing off
2. **How a REST URL reaches your method** — the `DispatcherServlet` chain (asked verbatim)
3. **Why `@Transactional` silently fails** — proxying, self-invocation, checked exceptions
4. **The N+1 problem** — what it is, how you found it, how you fixed it
5. **How you'd test this service** — slice tests vs full context, and your honest position on TDD
