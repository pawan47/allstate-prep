# Allstate Interview Dojo — Java

A working Maven + JUnit 5 project for preparing the Allstate India Java/Spring Boot loop.
Every problem in here is one **actually reported by Allstate candidates**, not a generic list.

> ### 👋 New here / no Java installed?
> Read **[SETUP.md](SETUP.md)** first — JDK install steps for macOS, Windows and Linux.
> You need **only a JDK 17+**. Maven is *not* required; this repo ships the Maven Wrapper.

## Start here

```bash
./mvnw -q test -Dtest=SetupSmokeTest
```

On Windows use `.\mvnw.cmd` instead of `./mvnw` everywhere below.
The **first run takes 1–3 minutes** while the wrapper downloads Maven and the test
dependencies; after that it's seconds.

Green (no output) means the dojo works. Then open **[DAY1-JAVA.md](DAY1-JAVA.md)** and work the blocks.

- **[SETUP.md](SETUP.md)** — install Java, run your first test, IDE setup, troubleshooting
- **[PLAN.md](PLAN.md)** — the 7-day plan and the real 6-round Allstate pipeline
- **[DAY1-JAVA.md](DAY1-JAVA.md)** — today, block by block
- **[cheatsheets/01-core-java-qbank.md](cheatsheets/01-core-java-qbank.md)** — reported questions, answered at interview depth
- **[cheatsheets/02-java8-and-modern.md](cheatsheets/02-java8-and-modern.md)** — streams, lambdas, Optional, records
- **[reference-solutions/](reference-solutions/)** — verified solutions, `.java.txt` so you can't peek by accident

## Commands

```bash
# one problem at a time (this is how you should work)
./mvnw -q test -Dtest=MySetSpecTest
./mvnw -q test -Dtest=MyHashMapTest
./mvnw -q test -Dtest=LRUCacheTest
./mvnw -q test -Dtest=ArrayProblemsTest
./mvnw -q test -Dtest=StringProblemsTest

# your own TDD scratchpad — empty on purpose
./mvnw -q test -Dtest=TddDrillTest

# a single nested group
./mvnw -q test -Dtest='ArrayProblemsTest$RotatedSearch'

# everything
./mvnw -q test
```

## The four proof demos

Run these instead of memorising the collections and concurrency answers.

```bash
./mvnw -q compile
./mvnw -q exec:java -Dexec.mainClass=com.allstate.prep.demos.FailFastVsFailSafeDemo
./mvnw -q exec:java -Dexec.mainClass=com.allstate.prep.demos.HashMapVsConcurrentHashMapDemo
./mvnw -q exec:java -Dexec.mainClass=com.allstate.prep.demos.WaitVsSleepDemo
./mvnw -q exec:java -Dexec.mainClass=com.allstate.prep.demos.ShallowVsDeepCopyDemo
```

`HashMapVsConcurrentHashMapDemo` drops roughly 100,000 of 160,000 writes in front of you.
That's the answer to "why ConcurrentHashMap?" — you've *seen* it, not read it.

## State

All `src/main` problem classes are **stubs that throw** `UnsupportedOperationException`.
That's intentional: the tests are the spec, and they should be red until you write the code.
The 71 reference tests are complete and were verified green against the reference solutions.

Built and verified on Java 17 / Maven 3.9.11.
