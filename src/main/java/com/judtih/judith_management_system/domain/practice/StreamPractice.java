package com.judtih.judith_management_system.domain.practice;

import java.util.List;
import java.util.stream.Collectors;

/**
 * This has nothing to do with the calendar nor anything with anything actually. this is just practice page, and thus has no point withing the service it self.
 */

public class StreamPractice {

    List<Integer> numbers = List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
    List<String> names = List.of("Jason", "Alice", "Bob", "Charlie", "Amy");

    // 1. map - transform each element
    // multiply every number by 2
    List<Integer> doubled = numbers.stream()
            .map(n -> n * 2)
            .collect(Collectors.toList());
    // result: [2, 4, 6, 8, 10, 12, 14, 16, 18, 20]

    // 2. filter - keep only elements that match a condition
    // keep only numbers greater than 5
    List<Integer> greaterThanFive = numbers.stream()
            .filter(n -> n > 5)
            .collect(Collectors.toList());
    // result: [6, 7, 8, 9, 10]

    // 3. map + filter combined
    // keep names that start with 'A', then uppercase them
    List<String> aNames = names.stream()
            .filter(name -> name.startsWith("A"))
            .map(String::toUpperCase)
            .collect(Collectors.toList());
    // result: [ALICE, AMY]

    // 4. what we did in getEvents - convert one type to another
    // convert a list of names to their lengths
    List<Integer> nameLengths = names.stream()
            .map(name -> name.length())
            .collect(Collectors.toList());
    // result: [5, 5, 3, 7, 3]


    // ── LAMBDA ──────────────────────────────────────────────

    /** a lambda is an anonymous function - a function with no name, written inline
    // syntax: (parameter) -> expression

    // without lambda - you write a full method
    // private int doubleIt(int n) { return n * 2; }

    // with lambda - same thing, no method name needed
    // n -> n * 2

    // in a stream:
    // .map(n -> n * 2)       means: for each n, return n * 2
    // .filter(n -> n > 5)    means: for each n, keep it if n > 5
    // .filter(name -> name.startsWith("A"))   means: keep name if it starts with A


    // ── METHOD REFERENCE (::) ───────────────────────────────

    // :: is shorthand for a lambda that just calls one existing method
    // these two are identical:
    // .map(name -> name.length())   lambda
    // .map(String::length)          method reference

    // and these two are identical:
    // .map(name -> name.toUpperCase())
    // .map(String::toUpperCase)

    // use :: when you're just calling one method with no extra logic
    // use -> when you need to write actual logic inline (n -> n * 2)

     to call method you created
     .map(this::methodName)
     or
     .map(number -> this.methodName(number))

    in getEvents we used this::toResponse
    meaning: for each Google Event, call toResponse() on this class
    same as: .map(event -> this.toResponse(event))
     */


    // ── PRACTICE ─────────────────────────────────────────────

    List<Integer> practiceNumbers = List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
    List<String> practiceNames = List.of("Jason", "Alice", "Bob", "Charlie", "Amy");

    // Q1. from practiceNumbers, return only the even numbers
    // expected: [2, 4, 6, 8, 10]
    List<Integer> q1() {
        return practiceNumbers.stream()
                .filter(n -> n%2 == 0)
                .collect(Collectors.toList());
    }

    // Q2. from names, return each name with its length appended
    // expected: ["Jason5", "Alice5", "Bob3", "Charlie7", "Amy3"]
    List<String> q2() {
        return practiceNames.stream()
                .map(name -> name + name.length())
                .collect(Collectors.toList());
    }

    // Q3. from numbers, return numbers greater than 3, each multiplied by 10
    // expected: [40, 50, 60, 70, 80, 90, 100]
    List<Integer> q3() {
        return practiceNumbers.stream()
                .filter(n -> n > 3)
                .map(numbers -> numbers * 10)
                .collect(Collectors.toList());
    }

    // Q4. from names, return only names longer than 3 characters, lowercased
    // expected: ["jason", "alice", "charlie"]
    List<String> q4() {
        return practiceNames.stream()
                .filter(name -> name.length() > 3)
                .map(String::toLowerCase)
                .toList();
    }

    // Q5. from numbers, return each number converted to a String
    // expected: ["1", "2", "3", "4", "5", "6", "7", "8", "9", "10"]
    List<String> q5() {
        return practiceNumbers.stream()
                .map(this::convert) //.map(num -> this.convert(num))
                .toList();
    }

    private String convert(Integer num) {
        return String.valueOf(num);
    }
}
