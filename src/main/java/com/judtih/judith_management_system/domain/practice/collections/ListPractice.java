package com.judtih.judith_management_system.domain.practice.collections;

import java.util.*;

/**
 * LIST PRACTICE
 * =============
 *
 * A List is an ORDERED sequence. Duplicates allowed, elements accessible by index.
 *
 * Common implementations:
 *   ArrayList  — default. Backed by resizable array. O(1) get by index, O(n) insert/remove in middle.
 *   LinkedList — doubly-linked nodes. O(1) insert/remove at either end, O(n) get by index. Rarely needed.
 *
 * Creation:
 *   List.of("a","b")             → immutable
 *   new ArrayList<>()            → empty mutable
 *   new ArrayList<>(otherColl)   → mutable copy of another Collection
 *
 * Key methods:
 *   .add(x), .add(idx, x), .remove(idx), .remove(Object)
 *   .get(idx), .set(idx, x)
 *   .indexOf(x), .contains(x), .size(), .isEmpty()
 *   .subList(from, to)          → live view (careful — changes reflect in original)
 *   .sort(Comparator.naturalOrder()) or Collections.sort(list)
 *   .reversed()  (Java 21+)
 *
 * Streams (bridge to collections):
 *   list.stream().filter(...).map(...).toList()    → immutable List
 *   list.stream().collect(Collectors.toList())     → mutable ArrayList
 */
public class ListPractice {

    public static void main(String[] args) {
        System.out.println("=== ListPractice ===\n");
        System.out.println("Quiz 1: " + quiz1());                                     // expected: 3
        System.out.println("Quiz 2: " + quiz2());                                     // expected: banana
        System.out.println("Quiz 3: " + quiz3(List.of(1,2,3,4,5)));                   // expected: 15
        System.out.println("Quiz 4: " + quiz4(List.of("hi","hello","hey")));          // expected: [HI, HELLO, HEY]
        System.out.println("Quiz 5: " + quiz5(List.of(3,1,4,1,5,9,2,6)));             // expected: [1, 1, 2, 3, 4, 5, 6, 9]
        System.out.println("Quiz 6: " + quiz6(List.of(3,1,4,1,5,9,2,6)));             // expected: [9, 6, 5, 4, 3, 2, 1, 1]
        System.out.println("Quiz 7: " + quiz7());                                     // expected: [c, b, a]
        System.out.println("Quiz 8: " + quiz8(List.of(1,2,3,4,5,6,7,8,9,10)));        // expected: [2, 4, 6, 8, 10]
        System.out.println("Quiz 9: " + quiz9(List.of("a","b","c","d","e")));         // expected: [b, c, d]
        System.out.println("Quiz 10: " + quiz10(List.of("apple","banana","apple"), "apple"));  // expected: 2
        System.out.println("Quiz 11: " + quiz11(List.of("apple","banana","cherry"))); // expected: [apple, banana, cherry] then [BANANA] — see printout
    }

    /** Quiz 1: Return the size of a list containing "a", "b", "c". */
    static int quiz1() {
        // TODO
        return -1;
    }

    /** Quiz 2: Return the element at index 1 of ["apple","banana","cherry"]. */
    static String quiz2() {
        // TODO
        return null;
    }

    /** Quiz 3: Return the SUM of all integers in the given list. Use a for-each loop or streams. */
    static int quiz3(List<Integer> nums) {
        // TODO
        return -1;
    }

    /** Quiz 4: Return a new list where every string is UPPERCASED.
     *  Try both approaches: (a) manual for-loop, (b) stream + map + toList. */
    static List<String> quiz4(List<String> words) {
        // TODO
        return null;
    }

    /** Quiz 5: Return the given list SORTED ascending.
     *  Hint: input is immutable, so make a mutable copy first. */
    static List<Integer> quiz5(List<Integer> nums) {
        // TODO
        return null;
    }

    /** Quiz 6: Return the given list sorted DESCENDING.
     *  Hint: Comparator.reverseOrder() or Comparator.naturalOrder().reversed(). */
    static List<Integer> quiz6(List<Integer> nums) {
        // TODO
        return null;
    }

    /** Quiz 7: Build ["a","b","c"] then reverse it in place. Return the reversed list. */
    static List<String> quiz7() {
        // TODO
        return null;
    }

    /** Quiz 8: Return only the EVEN numbers from the given list. */
    static List<Integer> quiz8(List<Integer> nums) {
        // TODO
        return null;
    }

    /** Quiz 9: Return a sublist skipping the first and last element.
     *  Hint: subList(from, to) is exclusive on 'to'. */
    static List<String> quiz9(List<String> items) {
        // TODO
        return null;
    }

    /** Quiz 10: Count how many times `target` appears in the list.
     *  Try both approaches: manual loop, and streams (.filter(...).count()). */
    static long quiz10(List<String> items, String target) {
        // TODO
        return -1;
    }

    /** Quiz 11: Print the original list, then filter to only strings LONGER than 5 chars,
     *  uppercase them, and return the result. */
    static List<String> quiz11(List<String> items) {
        System.out.println("(quiz 11 original) " + items);
        // TODO
        return null;
    }
}
