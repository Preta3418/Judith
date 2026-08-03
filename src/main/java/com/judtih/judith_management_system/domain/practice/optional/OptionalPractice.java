package com.judtih.judith_management_system.domain.practice.optional;

import java.util.*;

/**
 * OPTIONAL PRACTICE
 * =================
 *
 * Optional<T> is a container that may or may not hold a value. Instead of returning
 * null and forcing every caller to remember to null-check, you return Optional<T>
 * and the type system reminds them.
 *
 * You use Optionals ALL THE TIME in Spring — every JpaRepository.findById returns
 * Optional<T>, every ...findByEmail(email) that could miss returns Optional<T>.
 *
 * Creation:
 *   Optional.empty()              → guaranteed empty
 *   Optional.of(x)                → throws NPE if x is null; use when you're SURE it's non-null
 *   Optional.ofNullable(x)        → empty if x is null, present otherwise — the safe factory
 *
 * Checking presence (avoid if possible; prefer functional methods below):
 *   .isPresent()   .isEmpty()   .ifPresent(x -> ...)   .ifPresentOrElse(x -> ..., () -> ...)
 *
 * Unwrapping (produces the value or a default / exception):
 *   .orElse(default)              → default value (evaluated even if present — beware if expensive)
 *   .orElseGet(() -> default)     → lazy default via supplier (evaluated only if empty)
 *   .orElseThrow()                → NoSuchElementException if empty
 *   .orElseThrow(() -> new X())   → your own exception if empty
 *   .get()                        → AVOID — throws if empty, defeats the whole point
 *
 * Transforming (returns a new Optional):
 *   .map(x -> f(x))               → apply function if present, else stay empty
 *   .flatMap(x -> optionalReturningFn(x))   → same but flattens Optional<Optional<Y>>
 *   .filter(x -> pred(x))         → keep value only if predicate matches
 *
 * Rule of thumb: chain map/filter/flatMap, then finish with orElse/orElseThrow.
 * Never write `if (opt.isPresent()) opt.get()...` — use .map or .ifPresent instead.
 *
 * How to run:
 *   Right-click file → Run 'OptionalPractice.main()'. Each quiz prints yours vs expected.
 */
public class OptionalPractice {

    public static void main(String[] args) {
        System.out.println("=== OptionalPractice ===\n");
        System.out.println("Quiz 1:  " + quiz1());                                  // expected: false
        System.out.println("Quiz 2:  " + quiz2());                                  // expected: true
        System.out.println("Quiz 3a: " + quiz3(null));                              // expected: (empty)
        System.out.println("Quiz 3b: " + quiz3("hello"));                           // expected: Optional[hello]
        System.out.println("Quiz 4:  " + quiz4(null));                              // expected: default
        System.out.println("Quiz 5:  " + quiz5("kim"));                             // expected: HELLO, KIM
        System.out.println("Quiz 6a: " + quiz6("42"));                              // expected: Optional[42]
        System.out.println("Quiz 6b: " + quiz6("nope"));                            // expected: Optional.empty
        System.out.println("Quiz 7:  " + quiz7(List.of("apple","banana")));         // expected: APPLE
        System.out.println("Quiz 7:  " + quiz7(List.of()));                         // expected: none
        System.out.println("Quiz 8:  " + quiz8("apple"));                           // expected: 5
        System.out.println("Quiz 8:  " + quiz8(null));                              // expected: 0
        try { quiz9(null); } catch (Exception e) { System.out.println("Quiz 9:  threw " + e.getClass().getSimpleName()); }
        System.out.println("Quiz 10: " + quiz10(new User("alice", new Address("Seoul"))));   // expected: Seoul
        System.out.println("Quiz 10: " + quiz10(new User("bob", null)));                     // expected: (no city)
        System.out.println("Quiz 10: " + quiz10(null));                                       // expected: (no city)
        System.out.println("Quiz 11: " + quiz11(Map.of("a", 1, "b", 2), "a"));      // expected: found: 1
        System.out.println("Quiz 11: " + quiz11(Map.of("a", 1, "b", 2), "z"));      // expected: not found
        System.out.println("Quiz 12: " + quiz12("kim@judith.com"));                  // expected: judith.com
        System.out.println("Quiz 12: " + quiz12("noatsign"));                        // expected: (no domain)
    }

    /** Quiz 1: Create Optional.empty() and return whether it isPresent(). */
    static boolean quiz1() {
        // TODO
        return true;
    }

    /** Quiz 2: Create Optional.of("hello") and return whether it isPresent(). */
    static boolean quiz2() {
        // TODO
        return false;
    }

    /** Quiz 3: Wrap the given (possibly null) value using ofNullable. Return the Optional as-is
     *  so println shows Optional[...] or Optional.empty. */
    static Optional<String> quiz3(String value) {
        // TODO
        return null;
    }

    /** Quiz 4: Wrap the given (possibly null) String. Return "default" if empty, otherwise the value. */
    static String quiz4(String value) {
        // TODO
        return "";
    }

    /** Quiz 5: Given a name, wrap it in Optional. If present, transform to "HELLO, {NAME}" (uppercased).
     *  If empty, return "no one". */
    static String quiz5(String name) {
        // TODO
        return "";
    }

    /** Quiz 6: Return Optional<Integer> from parsing the given string. Empty if it fails.
     *  Hint: try/catch around Integer.parseInt inside the method that returns Optional. */
    static Optional<Integer> quiz6(String s) {
        // TODO
        return Optional.empty();
    }

    /** Quiz 7: Return the FIRST element of the list uppercased, or "none" if empty.
     *  Hint: use list.stream().findFirst() which returns Optional. */
    static String quiz7(List<String> list) {
        // TODO
        return "";
    }

    /** Quiz 8: Return the LENGTH of the given nullable string, or 0 if null.
     *  Hint: Optional.ofNullable(s).map(String::length).orElse(0) */
    static int quiz8(String s) {
        // TODO
        return -1;
    }

    /** Quiz 9: Given a nullable value, throw IllegalArgumentException("value required") if empty.
     *  Otherwise return the value in uppercase. Hint: orElseThrow. */
    static String quiz9(String value) {
        // TODO
        return "";
    }

    /** Quiz 10: Given a (nullable) User with a (nullable) Address, return the city.
     *  If any link in the chain is missing, return "(no city)".
     *  This is the pattern where Optional shines vs nested null checks. */
    static String quiz10(User user) {
        // TODO
        // Hint: Optional.ofNullable(user).map(User::address).map(Address::city).orElse("(no city)")
        return "";
    }

    /** Quiz 11: Look up the key in the map. If found, return "found: {value}". If missing, "not found".
     *  Hint: Optional.ofNullable(map.get(key)).map(v -> "found: " + v).orElse("not found") */
    static String quiz11(Map<String, Integer> map, String key) {
        // TODO
        return "";
    }

    /** Quiz 12: Given an email (may or may not contain '@'), return the domain portion,
     *  or "(no domain)" if there's no @ sign or if the input is empty.
     *  Hint: chain map + filter + map to isolate the part after @. */
    static String quiz12(String email) {
        // TODO
        return "";
    }

    // Toy types for chained-null quiz
    record Address(String city) {}
    record User(String name, Address address) {}
}
