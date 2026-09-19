package com.allstate.prep.demos;

/**
 * ANSWERS A REPORTED ALLSTATE QUESTION:
 *   "What is difference between wait and sleep?"
 *
 * Run it:  mvn -q compile exec:java -Dexec.mainClass=com.allstate.prep.demos.WaitVsSleepDemo
 *
 * Most candidates say "wait releases the lock, sleep doesn't" and stop there.
 * This prints the proof, and the block at the bottom is the full answer.
 */
public class WaitVsSleepDemo {

    private static final Object LOCK = new Object();

    public static void main(String[] args) throws Exception {
        sleepHoldsTheLock();
        Thread.sleep(200);
        System.out.println();
        waitReleasesTheLock();
        printAnswer();
    }

    private static void sleepHoldsTheLock() throws Exception {
        System.out.println("=== Thread.sleep() inside synchronized: KEEPS the monitor ===");
        Thread sleeper = new Thread(() -> {
            synchronized (LOCK) {
                System.out.println("  [sleeper] got the lock, sleeping 500ms while HOLDING it");
                try { Thread.sleep(500); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
                System.out.println("  [sleeper] awake, releasing lock");
            }
        }, "sleeper");

        Thread contender = new Thread(() -> {
            long t0 = System.currentTimeMillis();
            synchronized (LOCK) {
                System.out.println("  [contender] finally got in after "
                        + (System.currentTimeMillis() - t0) + "ms of BLOCKING");
            }
        }, "contender");

        sleeper.start();
        Thread.sleep(50);          // let sleeper win the lock first
        contender.start();
        sleeper.join();
        contender.join();
    }

    private static void waitReleasesTheLock() throws Exception {
        System.out.println("=== Object.wait() inside synchronized: RELEASES the monitor ===");
        Thread waiter = new Thread(() -> {
            synchronized (LOCK) {
                System.out.println("  [waiter] got the lock, calling wait() — lock is now FREE");
                try {
                    LOCK.wait();    // releases LOCK, parks until notified
                } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
                System.out.println("  [waiter] notified, reacquired the lock, done");
            }
        }, "waiter");

        Thread notifier = new Thread(() -> {
            long t0 = System.currentTimeMillis();
            synchronized (LOCK) {
                System.out.println("  [notifier] walked straight in after "
                        + (System.currentTimeMillis() - t0) + "ms — no blocking, because wait() let go");
                LOCK.notify();
            }
        }, "notifier");

        waiter.start();
        Thread.sleep(100);
        notifier.start();
        waiter.join();
        notifier.join();
    }

    private static void printAnswer() {
        System.out.println("""

            ── THE ANSWER TO GIVE ──────────────────────────────────────────
                              wait()                    sleep()
            declared on       java.lang.Object          java.lang.Thread
            static?           no (instance monitor)     yes (acts on current)
            monitor lock      RELEASES it               KEEPS it
            must hold lock?   YES — else IllegalMonitorStateException
            woken by          notify()/notifyAll(),     timeout expiry,
                              timeout, interrupt        interrupt
            purpose           inter-thread coordination pure delay / throttle
            thread state      WAITING / TIMED_WAITING   TIMED_WAITING

            Two things that score extra:

            1. ALWAYS call wait() in a loop, never in an if:
                   synchronized (lock) {
                       while (!conditionMet) lock.wait();   // guards spurious wakeups
                       // ...proceed
                   }
               A spurious wakeup is permitted by the JLS. An `if` here is a
               real, shipped-to-prod bug class.

            2. In modern code you rarely write either. Prefer
               java.util.concurrent: BlockingQueue for producer/consumer,
               CountDownLatch / CyclicBarrier for rendezvous,
               Condition.await()/signal() when you need multiple wait sets on
               one lock. Say this — it signals you've worked past the textbook.
            ────────────────────────────────────────────────────────────────""");
    }
}
