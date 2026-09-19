# Setup — from zero to running tests

You need **one** thing installed: a **Java JDK (version 17 or newer)**.

You do **not** need to install Maven. This project ships the *Maven Wrapper*
(`mvnw`), which downloads the correct Maven version by itself on first run.

Total time: about 5 minutes.

---

## Step 1 — Install a JDK

Pick your operating system. **Temurin 17** is the recommended build (free, no account needed).

### macOS

With [Homebrew](https://brew.sh):

```bash
brew install --cask temurin@17
```

No Homebrew? Download the `.pkg` installer for your chip (Apple Silicon = **aarch64**,
Intel = **x64**) from [adoptium.net/temurin/releases/?version=17](https://adoptium.net/temurin/releases/?version=17)
and run it.

### Windows

With [winget](https://learn.microsoft.com/en-us/windows/package-manager/winget/) (built into Windows 10/11):

```powershell
winget install EclipseAdoptium.Temurin.17.JDK
```

Or with [Chocolatey](https://chocolatey.org/):

```powershell
choco install temurin17
```

Or download the `.msi` from [adoptium.net](https://adoptium.net/temurin/releases/?version=17).
**During install, tick "Set JAVA_HOME variable"** — it saves a step later.

> After installing, **close and reopen** your terminal so the PATH change takes effect.

### Linux — Debian / Ubuntu

```bash
sudo apt update && sudo apt install -y openjdk-17-jdk
```

### Linux — Fedora / RHEL / CentOS

```bash
sudo dnf install -y java-17-openjdk-devel
```

### Linux — Arch

```bash
sudo pacman -S jdk17-openjdk
```

---

## Step 2 — Verify Java is installed

```bash
java -version
javac -version
```

You should see **17** or higher, for example:

```
openjdk version "17.0.16" 2025-07-15
```

Both commands must work. If `java` works but `javac` says "command not found",
you installed a **JRE** instead of a **JDK** — go back to Step 1 and install the JDK.

---

## Step 3 — Get the code

```bash
git clone https://github.com/pawan47/allstate-prep.git
cd allstate-prep
```

No Git? Install it (`brew install git` / `winget install Git.Git` /
`sudo apt install git`), or download the ZIP from the GitHub page
("Code" → "Download ZIP") and unzip it.

---

## Step 4 — Run the tests

**macOS / Linux:**

```bash
./mvnw test -Dtest=SetupSmokeTest
```

**Windows (PowerShell or CMD):**

```powershell
.\mvnw.cmd test -Dtest=SetupSmokeTest
```

The **first run takes 1–3 minutes** — the wrapper is downloading Maven (~10 MB) and the
JUnit/AssertJ dependencies. That's a one-time cost; later runs take a couple of seconds.

You want to see:

```
Tests run: 1, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

That's it — you're set up. Now read [DAY1-JAVA.md](DAY1-JAVA.md).

---

## Step 5 — Run everything (optional check)

```bash
./mvnw test
```

⚠️ **Expect around 60 failures, and that is correct.** This is a practice repo:
the classes in `src/main/java` are deliberately **empty stubs** that throw
`UnsupportedOperationException`, and the tests are the **specification** you
implement against. Failing tests are the starting line, not a broken setup.

Only `SetupSmokeTest` is meant to pass on a fresh clone. If *that* one passes,
your environment is fine.

To work one problem at a time (the way you should):

```bash
./mvnw test -Dtest=MySetSpecTest
./mvnw test -Dtest=LRUCacheTest
./mvnw test -Dtest=ArrayProblemsTest
./mvnw test -Dtest=StringProblemsTest
./mvnw test -Dtest=MyHashMapTest
```

And the four runnable demo programs:

```bash
./mvnw compile
./mvnw exec:java -Dexec.mainClass=com.allstate.prep.demos.FailFastVsFailSafeDemo
./mvnw exec:java -Dexec.mainClass=com.allstate.prep.demos.HashMapVsConcurrentHashMapDemo
./mvnw exec:java -Dexec.mainClass=com.allstate.prep.demos.WaitVsSleepDemo
./mvnw exec:java -Dexec.mainClass=com.allstate.prep.demos.ShallowVsDeepCopyDemo
```

---

## Optional — use an IDE instead of the terminal

An IDE makes the red/green TDD loop much faster. Either is free.

**IntelliJ IDEA Community Edition** ([download](https://www.jetbrains.com/idea/download/)) —
the standard choice for Java.
1. *File → Open* → select the `allstate-prep` **folder** (the one with `pom.xml`)
2. It detects Maven and imports automatically — wait for indexing to finish
3. If prompted for a Project SDK, choose your **JDK 17**
4. Click the green ▶ next to any test class or method, or press **Ctrl+Shift+F10**

**VS Code** ([download](https://code.visualstudio.com/)) —
install the [Extension Pack for Java](https://marketplace.visualstudio.com/items?itemName=vscjava.vscode-java-pack),
then *File → Open Folder* → `allstate-prep`. Tests get ▶ icons in the gutter and a
Testing panel in the sidebar.

---

## Troubleshooting

**`./mvnw: Permission denied`** (macOS/Linux)
```bash
chmod +x mvnw
```

**`JAVA_HOME is not set`** — point it at your JDK, then reopen the terminal:
```bash
# macOS (Homebrew Temurin)
echo 'export JAVA_HOME=$(/usr/libexec/java_home -v 17)' >> ~/.zshrc && source ~/.zshrc

# Linux
echo 'export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64' >> ~/.bashrc && source ~/.bashrc
```
```powershell
# Windows PowerShell (adjust the version folder to what you installed)
[Environment]::SetEnvironmentVariable('JAVA_HOME','C:\Program Files\Eclipse Adoptium\jdk-17.0.16.8-hotspot','User')
```

**`WARNING: sun.misc.Unsafe::staticFieldBase has been called`** — harmless noise from
Maven itself when it runs on a very new JDK (21+). It does not affect the build.
Ignore it, or run on JDK 17 to silence it. Add `-q` to hide all non-error output.

**`release version 17 not supported`** — your JDK is older than 17. Check with
`javac -version` and install 17+.

**First run hangs or fails to download** — the wrapper needs internet access to fetch
Maven and dependencies. Behind a corporate proxy, set `HTTP_PROXY` / `HTTPS_PROXY`,
or copy a working `~/.m2/settings.xml` into place.

**`Could not resolve dependencies` / corrupted download** — clear the cache and retry:
```bash
rm -rf ~/.m2/repository/org/junit ~/.m2/repository/org/assertj
./mvnw test -Dtest=SetupSmokeTest
```

---

## What's in here

| Path | What it is |
|---|---|
| [README.md](README.md) | Overview and command reference |
| [PLAN.md](PLAN.md) | 7-day interview prep plan + the real interview pipeline |
| [DAY1-JAVA.md](DAY1-JAVA.md) | Day 1, broken into timed blocks — **start here** |
| [cheatsheets/](cheatsheets/) | Reported questions, answered at interview depth |
| `src/main/java/...` | **Stubs you implement** (they throw on purpose) |
| `src/test/java/...` | 71 reference tests — the specification |
| `src/test/.../TddDrillTest.java` | **Empty on purpose** — your TDD scratchpad |
| `src/main/.../demos/` | 4 runnable programs that prove the tricky answers |
| [reference-solutions/](reference-solutions/) | Verified solutions, as `.txt` so you can't peek by accident |
