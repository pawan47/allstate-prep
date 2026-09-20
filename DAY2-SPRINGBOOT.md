# Day 2 — Spring Boot

**Why today matters:** their L1 round is ~2 hours covering *"all concepts of Core Java,
Springboot, Microservices, Apache Kafka, AWS, Azure DevOps CI/CD and Spring Native APIs"*
and requires you to write *"an end-to-end REST API with all the key features."*

That's the whole shape of today: **know the mechanisms, and be able to type a REST resource
under time pressure.**

Your reference all day: **[cheatsheets/04-spring-boot-qbank.md](cheatsheets/04-spring-boot-qbank.md)**

---

## Route

- **Full (6h)** — all 6 blocks
- **Short (3h)** — Blocks 1, 2, 3, 6. Block 2 is non-negotiable.

Same rule as Day 1: **nothing gets "read."** Every item is typed as code or said out loud.

---

## Block 1 — The five mechanisms (60 min) ⭐ START HERE

These five are the highest-probability Spring questions. Study them in this order, and for
each one **close the file and say the answer aloud** before moving on.

Read: [04-spring-boot-qbank.md](cheatsheets/04-spring-boot-qbank.md), sections 1–4.

| # | Mechanism | The specific thing to be able to say |
|---|-----------|--------------------------------------|
| 1 | **Auto-configuration** | Candidates listed in `AutoConfiguration.imports`, applied via `@ConditionalOnClass` / `@ConditionalOnMissingBean` — which is *why your own bean silently wins* |
| 2 | **REST URL → method** 🎯 | The `DispatcherServlet` → `HandlerMapping` → `HandlerAdapter` → converter chain. **Asked verbatim at Allstate.** |
| 3 | **`@Transactional`** | It's a **proxy**, so self-invocation does nothing, `private` can't be advised, and checked exceptions **commit** by default |
| 4 | **DI + bean resolution** | Constructor injection and why; type → `@Primary` → `@Qualifier` → name |
| 5 | **Bean scopes** | `singleton` is *per container*; a prototype injected into a singleton is captured **once** |

**Self-test — answer without looking:**
- [ ] Why does defining your own `DataSource` bean stop Boot creating one?
- [ ] A request hits `/api/orders/5` — trace it to your method, naming four components.
- [ ] Why might `@Transactional` do nothing at all on a method that clearly has it?
- [ ] Two `PaymentService` beans exist. What does Spring do, in order?
- [ ] What are the three annotations inside `@SpringBootApplication`?

---

## Block 2 — Build a REST resource, timed (90 min) ⭐ THE ONE THAT MATTERS

They ask you to write one. So write one — **twice**.

### Generate a fresh project (2 min)

```bash
cd ~/Desktop && curl -sS -G https://start.spring.io/starter.zip \
  -d dependencies=web,data-jpa,validation,h2,actuator \
  -d type=maven-project -d language=java -d javaVersion=17 \
  -d groupId=com.practice -d artifactId=policy-api \
  -d name=policy-api -d packageName=com.practice.policyapi \
  -o policy-api.zip && unzip -q -o policy-api.zip -d policy-api && cd policy-api/policy-api && ls
```

Then confirm it builds and boots:

```bash
./mvnw -q test && ./mvnw spring-boot:run
```

**Verified working** — this generates Spring Boot **4.1.1** on Java 17 and the build passes.
No `bootVersion` is pinned on purpose: Initializr only serves the current line, and a stale
pin returns an error. If Allstate's codebase is on Boot 3.x, everything today still applies
— 3.x and 4.x share the same programming model, and the `javax` → `jakarta` migration
already happened back in Boot 3.

> Picked an insurance-flavoured domain on purpose — a `Policy` resource is a natural fit for
> an Allstate interview and costs you nothing.

### The spec — "all the key features" (45 min, timer on)

Build `Policy` with: `id`, `policyNumber`, `holderName`, `premium`, `startDate`, `active`.

- [ ] **Entity** — `@Entity`, `@Id @GeneratedValue`, a `@Version` field for optimistic locking
- [ ] **Repository** — `extends JpaRepository<Policy, Long>`, plus one derived query (`findByActiveTrue`) and one `@Query`
- [ ] **DTOs** — separate `PolicyRequest` and `PolicyResponse` records. **Never expose the entity.**
- [ ] **Mapper** — plain hand-written static methods are fine
- [ ] **Service** — interface + impl, constructor injection, `@Transactional` on writes and `readOnly = true` on reads
- [ ] **Controller** — `@RestController`, `@RequestMapping("/api/v1/policies")`, all five verbs
- [ ] **Validation** — `@NotBlank`, `@Positive`, `@Future` on the request DTO; `@Valid` on the parameter
- [ ] **Exception handling** — a `PolicyNotFoundException` + `@RestControllerAdvice` returning a consistent body (bonus: `ProblemDetail`)
- [ ] **Status codes** — **201 + `Location` header** on create, **204** on delete, 404 on missing, 400 on validation failure
- [ ] **Pagination** — `Page<PolicyResponse> findAll(Pageable pageable)`
- [ ] **Config** — an `application.yml` with an H2 datasource and a dev profile
- [ ] **Actuator** — confirm `/actuator/health` responds

### Then test it (30 min)

- [ ] `@WebMvcTest` on the controller with `MockMvc` + `@MockBean` on the service — assert status, JSON body via `jsonPath`, and a 400 on invalid input
- [ ] `@DataJpaTest` on the repository — save and query
- [ ] One `@SpringBootTest` smoke test that just proves the context loads

### Then do it again (the actual drill)

**Delete the project and rebuild it from scratch, aiming for 45 minutes, narrating aloud.**
The second pass is where it becomes muscle memory. Reproducing this under observation is
what the round actually tests.

### Verify by hand

```bash
curl -i -X POST localhost:8080/api/v1/policies \
  -H 'Content-Type: application/json' \
  -d '{"policyNumber":"P-1001","holderName":"Pawan","premium":499.99,"startDate":"2027-01-01"}'
# expect: 201 + Location header

curl -s localhost:8080/api/v1/policies?page=0&size=10
curl -i localhost:8080/api/v1/policies/999          # expect 404 with your error body
curl -i -X POST localhost:8080/api/v1/policies -H 'Content-Type: application/json' -d '{}'
# expect 400 with field-level validation messages
```

---

## Block 3 — Data & JPA depth (45 min)

Read: [04-spring-boot-qbank.md](cheatsheets/04-spring-boot-qbank.md) § 4.

- [ ] **The N+1 problem** ⭐ — what it is, and have a **specific story**: how you *detected* it (SQL logging, `spring.jpa.show-sql`, an APM trace) and how you fixed it (`JOIN FETCH` / `@EntityGraph` / `@BatchSize`). The detection half is the experience signal.
- [ ] **Lazy vs eager**, and why `LazyInitializationException` happens outside a transaction
- [ ] **Entity lifecycle** — transient → persistent → detached → removed
- [ ] **Propagation** — `REQUIRED` vs `REQUIRES_NEW`, and one case where you'd want each
- [ ] **Optimistic (`@Version`) vs pessimistic locking**
- [ ] **Why never `ddl-auto=update` in production** — use Flyway/Liquibase
- [ ] Prove N+1 to yourself: add `spring.jpa.show-sql=true` to the project from Block 2, create a `@OneToMany`, and count the queries in the log

---

## Block 4 — Legacy SOAP (30 min)

Four SOAP questions are in Allstate's reported bank. They're an insurer — the legacy is
real, and 30 minutes is cheap insurance.

Read: [01-core-java-qbank.md § 6](cheatsheets/01-core-java-qbank.md).

- [ ] 🎯 What is a **WSDL** — the five parts (types, message, portType, binding, service/port)
- [ ] 🎯 What is an **endpoint**
- [ ] 🎯 What is **`wsimport`** — and that it was **removed from the JDK in Java 11** (JEP 320), so you now use the `jaxws-maven-plugin`. That detail lands well.
- [ ] 🎯 **First step to consume a SOAP service** → obtain the WSDL, then generate stubs
- [ ] **SOAP vs REST** — protocol vs architectural style, and *when you'd pick SOAP* (formal contracts, message-level security, existing partner integrations — common in insurance)

---

## Block 5 — Security, config, ops (45 min)

- [ ] **Spring Security basics** — the filter chain; authentication vs authorisation; `SecurityFilterChain` bean config (the modern style, not `WebSecurityConfigurerAdapter`); `@PreAuthorize`
- [ ] **JWT flow** — how a stateless API validates a token per request, and why that suits microservices
- [ ] **CORS** vs **CSRF** — and why CSRF protection is usually disabled for a stateless token API
- [ ] **Secrets** — never in `application.yml`; env vars, Key Vault / Secrets Manager. Ties to 12-Factor.
- [ ] **Property precedence** — command line > env vars > profile files > `application.yml`
- [ ] **Actuator** — which endpoints are exposed by default (only `health` and `info`) and how you'd secure the rest
- [ ] **12-Factor** ⭐ — named in their engineering standards. Be fluent on **config, logs, stateless processes, disposability**.
- [ ] **Azure DevOps CI/CD** ⭐ — `azure-pipelines.yml`, stages/jobs/steps, service connections, variable groups + Key Vault, agents, approval gates. **If your experience is Jenkins, map the terms across out loud** — that transfer is the answer they want.

---

## Block 6 — Q&A out loud (40 min)

Work the whole of [04-spring-boot-qbank.md](cheatsheets/04-spring-boot-qbank.md): cover the
answer, say yours, reveal. Then do Section 8 of
[03-java-study-guide.md](cheatsheets/03-java-study-guide.md) — build, CI/CD, cloud.

**Two questions to rehearse properly, because they're conversational and easy to fumble:**

1. **"Walk me through the architecture of your current project."** One page, drawn. Every box: why it exists, what you'd change, what broke once.
2. **"Do you write tests first?"** Allstate pairs on TDD daily — they will find out. Give an honest, concrete answer about what you actually do, and say you're comfortable pairing in a TDD flow. Don't claim a practice you can't demonstrate next round.

---

## End-of-day check

You're done when you can, from an empty directory and with no notes:

- [ ] Scaffold and run a Spring Boot app in under 5 minutes
- [ ] Build a full REST resource — DTOs, validation, advice, 201+Location, 204, pagination — in ~45 minutes, narrating
- [ ] Trace a request from the socket to your controller method, naming four components
- [ ] Explain auto-configuration mechanically, including `@ConditionalOnMissingBean`
- [ ] Give the three reasons `@Transactional` can silently do nothing
- [ ] Explain the N+1 problem, how you'd detect it, and two fixes
- [ ] Write a `@WebMvcTest` and a `@DataJpaTest` from memory
- [ ] Answer all four SOAP questions
- [ ] Name the 12-Factor items that matter and tie two to real config decisions

7 of 9 and Day 2 did its job. **Tomorrow: microservices + design patterns** ([PLAN.md](PLAN.md) Day 3).
