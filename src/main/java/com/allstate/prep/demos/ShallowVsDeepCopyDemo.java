package com.allstate.prep.demos;

import java.util.ArrayList;
import java.util.List;

/**
 * ANSWERS A REPORTED ALLSTATE QUESTION:
 *   "What is shallow and deep copy?"
 * ...and sets up the follow-up they love: "in how many ways can an object be
 * instantiated?" (also reported — see the notes at the bottom).
 *
 * Run it:  mvn -q compile exec:java -Dexec.mainClass=com.allstate.prep.demos.ShallowVsDeepCopyDemo
 */
public class ShallowVsDeepCopyDemo {

    static class Address {
        String city;
        Address(String city) { this.city = city; }
        @Override public String toString() { return city; }
    }

    static class Employee implements Cloneable {
        String name;
        Address address;            // mutable reference — the whole problem
        List<String> skills;

        Employee(String name, Address address, List<String> skills) {
            this.name = name;
            this.address = address;
            this.skills = skills;
        }

        /** Object.clone() gives you a SHALLOW copy: references are shared. */
        @Override
        protected Employee clone() throws CloneNotSupportedException {
            return (Employee) super.clone();
        }

        /** DEEP copy: every mutable reachable object is copied too. */
        Employee deepCopy() {
            return new Employee(this.name,                       // String is immutable, safe to share
                                new Address(this.address.city),  // fresh Address
                                new ArrayList<>(this.skills));   // fresh list
        }

        @Override public String toString() {
            return name + " @ " + address + " " + skills;
        }
    }

    public static void main(String[] args) throws Exception {
        Employee original = new Employee("Pawan", new Address("Bangalore"),
                                          new ArrayList<>(List.of("Java")));

        System.out.println("=== SHALLOW copy via Object.clone() ===");
        Employee shallow = original.clone();
        shallow.name = "Clone";                      // String field: rebinding is safe
        shallow.address.city = "Pune";               // shared Address: MUTATES THE ORIGINAL
        shallow.skills.add("Spring");                // shared List: MUTATES THE ORIGINAL

        System.out.println("  original : " + original);
        System.out.println("  shallow  : " + shallow);
        System.out.println("  same Address object? " + (original.address == shallow.address));
        System.out.println("  -> The original's city changed to Pune and it grew a skill it");
        System.out.println("     never asked for. This is the classic production bug.");

        System.out.println("\n=== DEEP copy ===");
        Employee source = new Employee("Pawan", new Address("Bangalore"),
                                        new ArrayList<>(List.of("Java")));
        Employee deep = source.deepCopy();
        deep.address.city = "Pune";
        deep.skills.add("Spring");

        System.out.println("  source   : " + source);
        System.out.println("  deep     : " + deep);
        System.out.println("  same Address object? " + (source.address == deep.address));
        System.out.println("  -> source is untouched. Independent object graphs.");

        System.out.println("""

            ── THE ANSWER TO GIVE ──────────────────────────────────────────
            A shallow copy duplicates the object's fields as-is: primitives are
            copied by value, but every reference field still points at the SAME
            object. Object.clone() is shallow by default. So mutating a nested
            object through the copy is visible through the original.

            A deep copy recursively copies the entire reachable object graph, so
            the two are fully independent.

            Ways to deep copy, and their trade-offs:
              - hand-written copy constructor  -> fastest, explicit, my default
              - clone() overridden to clone nested fields -> Cloneable is a
                broken interface (marker with no clone method, protected
                method, no constructor invoked); Effective Java says avoid it
              - serialize + deserialise (Java serialization, Jackson, Gson)
                -> easy and generic, slow, needs everything serialisable
              - SerializationUtils.clone() from Apache Commons Lang
              - copy-on-write / immutable objects -> sidestep the question
                entirely, which is the real senior answer: make Address
                immutable (final fields, no setters) and sharing is safe, so
                a shallow copy is all you ever need. Records make this cheap.

            Watch out: List.copyOf / new ArrayList<>(other) are SHALLOW for the
            elements. A List<Employee> copy shares the Employee objects.
            ────────────────────────────────────────────────────────────────

            ── BONUS: "IN HOW MANY WAYS CAN AN OBJECT BE INSTANTIATED?" ────
            Reported at Allstate. There are six; most candidates name two.
              1. new keyword                       -> new Employee(...)
              2. reflection, no-arg               -> Class.forName("X").getDeclaredConstructor().newInstance()
                 (Class.newInstance() is deprecated since Java 9)
              3. reflection, any constructor      -> Constructor.newInstance(args)
              4. clone()                          -> no constructor is called
              5. deserialization                  -> no constructor is called
              6. factory / builder methods        -> e.g. List.of(), valueOf()
                 (delegates to new internally, but name it — it shows design sense)
            The point worth making: 4 and 5 BYPASS the constructor, which is
            why they break invariants and why singletons must defend against
            them (enum singleton, or throw from readResolve()).
            ────────────────────────────────────────────────────────────────""");
    }
}
