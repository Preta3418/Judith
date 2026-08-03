package com.judtih.judith_management_system.domain.practice.collections;

import java.util.*;

/**
 * SET PRACTICE
 * ============
 *
 * A Set holds UNIQUE elements. No duplicates, no positional access.
 *
 * Common implementations:
 *   HashSet       — fastest, no order guarantee, O(1) contains/add/remove
 *   LinkedHashSet — preserves insertion order, slightly slower than HashSet
 *   TreeSet       — kept in sorted order (natural or custom Comparator), O(log n)
 *
 * Creation shortcuts:
 *   Set.of("a", "b")            → immutable set (throws if you mutate)
 *   new HashSet<>()             → empty mutable set
 *   new HashSet<>(otherSet)     → mutable copy of otherSet
 *
 * Key methods on Set:
 *   .add(x)          → returns true if new, false if already present
 *   .remove(x)       → returns true if it existed
 *   .contains(x)     → O(1) on HashSet, O(n) on List (that's why Set exists)
 *   .size(), .isEmpty(), .forEach(), for-each loop
 *   .addAll(other)   → union (in place)
 *   .retainAll(other)→ intersection (in place)
 *   .removeAll(other)→ difference (in place)
 *
 * From Collections utility class:
 *   Collections.disjoint(a, b)  → true if NO overlap
 *   Collections.unmodifiableSet(s) → read-only view of s
 *
 * How to run:
 *   Right-click file → Run 'SetPractice.main()' in IntelliJ.
 *   Each quiz prints its result and expected value.
 */
public class SetPractice {

    public static void main(String[] args) {
        System.out.println("=== SetPractice ===\n");
        System.out.println("Quiz 1: " + quiz1());               // expected: 3
        System.out.println("Quiz 2: " + quiz2());               // expected: [red, green, blue] (any order)
        System.out.println("Quiz 3: " + quiz3("apple"));        // expected: true
        System.out.println("Quiz 3: " + quiz3("kiwi"));         // expected: false
        System.out.println("Quiz 4: " + quiz4());               // expected: 2
        System.out.println("Quiz 5: " + quiz5());               // expected: [b, c] (any order)
        System.out.println("Quiz 6: " + quiz6());               // expected: [a] (any order)
        System.out.println("Quiz 7: " + quiz7());               // expected: true
        System.out.println("Quiz 8: " + quiz8());               // expected: false
        System.out.println("Quiz 9: " + quiz9(List.of("a","a","b","c","c","c","d")));  // expected: 4
        System.out.println("Quiz 10: " + quiz10(Set.of("a","b","c"), Set.of("b","c","d")));  // expected: [a, b, c, d]
    }

    /** Quiz 1: Create a HashSet with "x", "y", "z" and return its size. */
    static int quiz1() {
        // TODO
        return -1;
    }

    /** Quiz 2: Return an immutable set containing exactly "red", "green", "blue". */
    static Set<String> quiz2() {
        // TODO
        return null;
    }

    /** Quiz 3: Create a set of fruits {"apple", "banana", "cherry"}
     *  and return whether it contains the given input. */
    static boolean quiz3(String fruit) {
        // TODO
        return false;
    }

    /** Quiz 4: Add "a", "b", "a", "c", "b" one at a time to a HashSet.
     *  Return the final size. (Test your understanding of "duplicates ignored".) */
    static int quiz4() {
        // TODO
        return -1;
    }

    /** Quiz 5: Return the INTERSECTION of {"a","b","c"} and {"b","c","d"} — the elements in both. */
    static Set<String> quiz5() {
        // TODO
        // Hint: make a copy of one, then retainAll the other. Set.of() sets are immutable.
        return null;
    }

    /** Quiz 6: Return the DIFFERENCE {"a","b","c"} MINUS {"b","c","d"} — elements only in the first. */
    static Set<String> quiz6() {
        // TODO
        return null;
    }

    /** Quiz 7: Do {"one","two"} and {"three","four"} have NO overlap?
     *  Use Collections.disjoint. */
    static boolean quiz7() {
        // TODO
        return false;
    }

    /** Quiz 8: Do {"one","two"} and {"two","three"} have NO overlap? */
    static boolean quiz8() {
        // TODO
        return false;
    }

    /** Quiz 9: Given a List (possibly with duplicates), return the count of DISTINCT elements.
     *  Use a Set. */
    static int quiz9(List<String> items) {
        // TODO
        return -1;
    }

    /** Quiz 10: Return the UNION of two sets, sorted alphabetically.
     *  (Hint: TreeSet keeps elements sorted.) */
    static Set<String> quiz10(Set<String> a, Set<String> b) {
        // TODO
        return null;
    }
}
