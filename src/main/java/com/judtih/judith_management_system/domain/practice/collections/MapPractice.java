package com.judtih.judith_management_system.domain.practice.collections;

import java.util.*;

/**
 * MAP PRACTICE
 * ============
 *
 * A Map stores KEY → VALUE pairs. Keys are unique; values can repeat.
 * Map is NOT a Collection (it's its own interface hierarchy).
 *
 * Common implementations:
 *   HashMap       — fastest, no order, O(1) get/put
 *   LinkedHashMap — preserves insertion order
 *   TreeMap       — keys kept sorted, O(log n)
 *
 * Creation:
 *   Map.of("k", 1, "j", 2)     → immutable, max ~10 entries
 *   Map.entry("k", 1)          → use inside Map.ofEntries(...) for many pairs
 *   new HashMap<>()            → empty mutable
 *   new HashMap<>(otherMap)    → mutable copy
 *
 * Key methods:
 *   .put(k, v)                 → returns the previous value (or null)
 *   .get(k)                    → returns value or null (careful!)
 *   .getOrDefault(k, def)      → safer than .get
 *   .containsKey(k), .containsValue(v)
 *   .remove(k)                 → returns old value
 *   .size(), .isEmpty()
 *   .keySet(), .values(), .entrySet()   ← iterate via one of these
 *
 * Idiomatic patterns:
 *   .computeIfAbsent(k, k2 -> new ArrayList<>()).add(x)   → group items into a Map<K, List<V>>
 *   .merge(k, 1, Integer::sum)                             → count occurrences
 *   .forEach((k, v) -> ...)                                → two-arg lambda
 *
 * Streams to Map:
 *   list.stream().collect(Collectors.toMap(keyFn, valueFn))
 *   list.stream().collect(Collectors.groupingBy(keyFn))    → Map<K, List<V>>
 */
public class MapPractice {

    public static void main(String[] args) {
        System.out.println("=== MapPractice ===\n");
        System.out.println("Quiz 1: " + quiz1());                                                    // expected: 25
        System.out.println("Quiz 2: " + quiz2());                                                    // expected: unknown
        System.out.println("Quiz 3: " + quiz3());                                                    // expected: 0
        System.out.println("Quiz 4: " + quiz4());                                                    // expected: true
        System.out.println("Quiz 5: " + quiz5(List.of("a","b","a","c","b","a")));                    // expected: {a=3, b=2, c=1}
        System.out.println("Quiz 6: " + quiz6(List.of("apple","banana","apricot","blueberry")));     // expected: {a=[apple, apricot], b=[banana, blueberry]}
        System.out.println("Quiz 7: " + quiz7(Map.of("k1",10, "k2",20, "k3",30)));                   // expected: 60
        System.out.println("Quiz 8: " + quiz8());                                                    // expected: [x, y, z] (any order)
        System.out.println("Quiz 9: " + quiz9(List.of("hi","hello","hey","cat"), 3));                // expected: {hi=2, hey=3, cat=3}
        System.out.println("Quiz 10: " + quiz10());                                                  // expected: two, three, four (order preserved)
    }

    /** Quiz 1: Create a HashMap with "age"→25, "score"→90. Return the value at key "age". */
    static Integer quiz1() {
        // TODO
        return null;
    }

    /** Quiz 2: Given a map with "name"→"kim", look up key "profession" using getOrDefault,
     *  defaulting to "unknown". Return that value. */
    static String quiz2() {
        // TODO
        return null;
    }

    /** Quiz 3: Show the danger of .get on missing key by returning:
     *  the SIZE of an empty HashMap after ONE call to get("missing").
     *  (get on a missing key does not add anything — the map stays empty.) */
    static int quiz3() {
        // TODO
        return -1;
    }

    /** Quiz 4: Put ("age", 25) into an empty map, then check whether the map containsKey("age"). Return that. */
    static boolean quiz4() {
        // TODO
        return false;
    }

    /** Quiz 5: Given a list of strings, return a Map<String, Integer> counting occurrences of each.
     *  Use .merge(k, 1, Integer::sum). */
    static Map<String, Integer> quiz5(List<String> items) {
        // TODO
        return null;
    }

    /** Quiz 6: Given a list of strings, group them by their FIRST letter.
     *  Return Map<Character, List<String>>.
     *  Try both: manual (computeIfAbsent) OR streams (Collectors.groupingBy). */
    static Map<Character, List<String>> quiz6(List<String> words) {
        // TODO
        return null;
    }

    /** Quiz 7: Return the SUM of all values in the given map. */
    static int quiz7(Map<String, Integer> m) {
        // TODO
        return -1;
    }

    /** Quiz 8: Create a map {"x"→1, "y"→2, "z"→3} and return its KEYSET as a Set<String>. */
    static Set<String> quiz8() {
        // TODO
        return null;
    }

    /** Quiz 9: Given a list of words and a target length, return Map<String, Integer>
     *  containing only the words whose length is >= target, mapped to their length.
     *  Example: (["hi","hello","hey","cat"], 3) → {"hello"=5, "hey"=3, "cat"=3}
     *  (Note: expected in comment uses toString of a HashMap which may reorder — that's fine.) */
    static Map<String, Integer> quiz9(List<String> words, int target) {
        // TODO
        return null;
    }

    /** Quiz 10: Build a LinkedHashMap so iteration ORDER matches insertion order.
     *  Insert ("one",1), ("two",2), ("three",3), ("four",4) in that order.
     *  Then remove "one". Return the keys in iteration order as a List. */
    static List<String> quiz10() {
        // TODO
        return null;
    }
}
