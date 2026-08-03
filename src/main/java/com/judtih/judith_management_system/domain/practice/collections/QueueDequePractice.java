package com.judtih.judith_management_system.domain.practice.collections;

import java.util.*;

/**
 * QUEUE & DEQUE PRACTICE
 * =======================
 *
 * Queue = FIFO (first in, first out) — like a line at the counter.
 * Deque = double-ended queue — add/remove from either end. Also usable as a stack (LIFO).
 *
 * These matter less day-to-day than List/Set/Map, but they show up in:
 *   - BFS/DFS traversal
 *   - Task queues / work buffers
 *   - Undo stacks (LIFO)
 *   - Sliding-window algorithms
 *
 * Common implementations:
 *   ArrayDeque       — default choice for both Queue and Deque. Fast, no capacity limit, no nulls.
 *   LinkedList       — implements both. Slower than ArrayDeque. Rarely the right choice.
 *   PriorityQueue    — orders elements by natural order or Comparator (NOT insertion order).
 *
 * Queue methods (two flavors — always prefer the "return special value" version):
 *   offer(x)    → add to tail. Returns false if full. (add(x) throws instead.)
 *   poll()      → remove and return head. Returns null if empty. (remove() throws.)
 *   peek()      → look at head without removing. Returns null if empty. (element() throws.)
 *
 * Deque adds:
 *   offerFirst(x), offerLast(x)
 *   pollFirst(),   pollLast()
 *   peekFirst(),   peekLast()
 *
 * Deque as a stack:
 *   push(x)  ≡ offerFirst(x)
 *   pop()    ≡ pollFirst()
 *   peek()   ≡ peekFirst()
 */
public class QueueDequePractice {

    public static void main(String[] args) {
        System.out.println("=== QueueDequePractice ===\n");
        System.out.println("Quiz 1: " + quiz1());                              // expected: apple
        System.out.println("Quiz 2: " + quiz2());                              // expected: [banana, cherry]
        System.out.println("Quiz 3: " + quiz3());                              // expected: null
        System.out.println("Quiz 4: " + quiz4(List.of(1,2,3,4,5)));            // expected: [5, 4, 3, 2, 1]
        System.out.println("Quiz 5: " + quiz5());                              // expected: c (last in, first out — stack behavior)
        System.out.println("Quiz 6: " + quiz6(List.of(3,1,4,1,5,9,2,6)));      // expected: 1 (smallest — PriorityQueue head)
        System.out.println("Quiz 7: " + quiz7(List.of(3,1,4,1,5,9,2,6)));      // expected: [1, 1, 2, 3, 4, 5, 6, 9]
    }

    /** Quiz 1: Create an ArrayDeque as a Queue.
     *  Offer "apple", "banana", "cherry" in order. Poll once. Return the polled value. */
    static String quiz1() {
        // TODO
        return null;
    }

    /** Quiz 2: Same setup as Quiz 1, but after polling once, return the queue's contents
     *  as a List<String> in order (head to tail). */
    static List<String> quiz2() {
        // TODO
        return null;
    }

    /** Quiz 3: Show that .poll() on an EMPTY queue returns null (doesn't throw).
     *  Return the result of polling an empty ArrayDeque<String>. */
    static String quiz3() {
        // TODO
        return null;
    }

    /** Quiz 4: Use a Deque as a STACK (LIFO).
     *  Push each number from the input list onto the stack.
     *  Then pop everything off and return in pop order (which should be reversed). */
    static List<Integer> quiz4(List<Integer> nums) {
        // TODO
        return null;
    }

    /** Quiz 5: Deque as stack. push "a", push "b", push "c", peek. Return what peek returned. */
    static String quiz5() {
        // TODO
        return null;
    }

    /** Quiz 6: Given numbers, feed them into a PriorityQueue. Return .peek() — the smallest. */
    static Integer quiz6(List<Integer> nums) {
        // TODO
        return null;
    }

    /** Quiz 7: Given numbers, feed them into a PriorityQueue, then poll everything out into a List.
     *  Result should be sorted ascending (that's how you use PriorityQueue as a sorter). */
    static List<Integer> quiz7(List<Integer> nums) {
        // TODO
        return null;
    }
}
