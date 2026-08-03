package com.judtih.judith_management_system.domain.practice.repository;

/**
 * SPRING DATA JPA — DERIVED QUERY PRACTICE
 * =========================================
 *
 * Spring Data JPA can generate SQL from method NAMES. You never write the query —
 * you just name the method following the grammar, and Spring wires the implementation.
 *
 * Basic shape:
 *   [findBy|existsBy|countBy|deleteBy][Property][Op][And|Or...][OrderBy...]
 *
 * Returns:
 *   findBy...   → List<T>  or  Optional<T>  (Optional if unique by nature: PK, unique column)
 *   existsBy... → boolean
 *   countBy...  → long / Long / Integer / int  (long is safest)
 *   deleteBy... → void or long (count deleted). Must be inside @Transactional.
 *
 * Property naming:
 *   Match the entity field name exactly, PascalCased after "By".
 *   Book has `title` → `findByTitle(String title)`
 *   Book has `deletedAt` → `findByDeletedAt(LocalDateTime dt)` or `findByDeletedAtIsNull()`
 *
 * Multi-condition:
 *   findByTitleAndPrice(String title, Integer price)
 *   findByTitleOrPrice(String title, Integer price)
 *
 * Operators:
 *   Is / Equals    (default)
 *   Not            findByTitleNot
 *   IsNull / IsNotNull
 *   Like / NotLike / StartingWith / EndingWith / Containing / IgnoreCase
 *   Lt / Lte / Gt / Gte / Between
 *   In(Collection)  → findByIdIn(List<Long> ids)
 *   OrderBy...Asc / OrderBy...Desc
 *   First / Top(N)  → findFirstByOrderByPriceDesc, findTop3ByOrderByPriceDesc
 *
 * Navigating FKs (property paths use underscore):
 *   Book has `Author author` → `findByAuthor_Name(String name)`
 *   or SpEL-safe: `findByAuthorName(String name)` (Spring can also resolve this if unambiguous)
 *   Best practice: use `_` for clarity when the FK entity has a field with the same name as another entity's field.
 *
 * Common gotchas:
 *   - Typo in property name → IllegalArgumentException at STARTUP, not runtime. Good.
 *   - "findByPrice(int)" and Book.price is Integer → OK, autoboxes.
 *   - Ordering matters. `findByTitleAndPriceGt` is fine; `findByPriceGtAndTitle` is also fine.
 *   - Multi-property "OrderBy": findByAuthor_NameOrderByPublishedAtDescTitleAsc
 *
 * How to use this file:
 *   Each quiz asks for a method NAME (or a signature). Return it as a String.
 *   main() prints your answer next to the expected one. Compare visually.
 *
 * The pretend schema is in SampleDomain.java.
 */
public class DerivedQueryPractice {

    public static void main(String[] args) {
        System.out.println("=== DerivedQueryPractice ===\n");
        print("Q1",  quiz1(),  "findByTitle");
        print("Q2",  quiz2(),  "findByAuthor_Name  (or findByAuthorName)");
        print("Q3",  quiz3(),  "existsByTitle");
        print("Q4",  quiz4(),  "countByAuthor_Id");
        print("Q5",  quiz5(),  "findByDeletedAtIsNull");
        print("Q6",  quiz6(),  "findByPriceBetween");
        print("Q7",  quiz7(),  "findByTitleContainingIgnoreCase");
        print("Q8",  quiz8(),  "findByAuthor_CountryAndDeletedAtIsNullOrderByPublishedAtDesc");
        print("Q9",  quiz9(),  "findTop3ByOrderByPriceDesc");
        print("Q10", quiz10(), "findByIdIn");
        print("Q11", quiz11(), "deleteByAuthor_Id");
        print("Q12", quiz12(), "findFirstByAuthor_IdOrderByPublishedAtDesc");
    }

    private static void print(String label, String actual, String expected) {
        String status = actual.equalsIgnoreCase(expected.split("\\s")[0]) ? "OK " : "?? ";
        System.out.println(status + label + " you: " + actual + "   |   expected: " + expected);
    }

    // ---------------- QUIZZES ----------------

    /** Q1: A method that finds all Books with an exact title match.
     *  Return type: List<Book>, param: String title.
     *  Return the METHOD NAME only. */
    static String quiz1() {
        // TODO
        return "";
    }

    /** Q2: A method that finds all Books by an author's name.
     *  (Author is an FK on Book; you're navigating Book.author.name)
     *  Return type: List<Book>. */
    static String quiz2() {
        // TODO
        return "";
    }

    /** Q3: A method that returns whether ANY Book has a given title.
     *  Return type: boolean, param: String title. */
    static String quiz3() {
        // TODO
        return "";
    }

    /** Q4: A method that counts Books written by a given authorId.
     *  Return type: long, param: Long authorId. */
    static String quiz4() {
        // TODO
        return "";
    }

    /** Q5: A method that returns Books whose deletedAt is null (not deleted).
     *  Return type: List<Book>, no params. */
    static String quiz5() {
        // TODO
        return "";
    }

    /** Q6: A method that returns Books with price between two values (inclusive on both ends).
     *  Return type: List<Book>, params: Integer low, Integer high. */
    static String quiz6() {
        // TODO
        return "";
    }

    /** Q7: A method that finds Books whose title CONTAINS a given substring, case-insensitive.
     *  Return type: List<Book>, param: String keyword. */
    static String quiz7() {
        // TODO
        return "";
    }

    /** Q8: Find all Books where author's country = X AND deletedAt is null, ordered by publishedAt DESC.
     *  Return type: List<Book>. */
    static String quiz8() {
        // TODO
        return "";
    }

    /** Q9: Return the top 3 most expensive Books.
     *  Return type: List<Book>, no params. */
    static String quiz9() {
        // TODO
        return "";
    }

    /** Q10: Find all Books whose id is in a given list.
     *  Return type: List<Book>, param: List<Long> ids. */
    static String quiz10() {
        // TODO
        return "";
    }

    /** Q11: Delete all Books by a given authorId. Return type: long. */
    static String quiz11() {
        // TODO
        return "";
    }

    /** Q12: Find the SINGLE most recently published Book by a given author.
     *  Return type: Optional<Book>. */
    static String quiz12() {
        // TODO
        return "";
    }
}
