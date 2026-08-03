package com.judtih.judith_management_system.domain.practice.sql;

/**
 * JPQL PRACTICE (@Query with JPQL)
 * =================================
 *
 * When derived query names get long / awkward, or you need JOIN / GROUP BY / DISTINCT /
 * subqueries, use @Query with JPQL.
 *
 * JPQL is SQL-like but operates on ENTITIES and their FIELD names, not tables/columns.
 *
 * Basic shape (in the repository interface):
 *
 *   @Query("SELECT b FROM Book b WHERE b.title = :title")
 *   List<Book> findByTitle(@Param("title") String title);
 *
 * Key syntax:
 *   - `FROM Book b`               — entity name, not table name
 *   - `b.author.name`             — property navigation via dot (no underscore here)
 *   - `:paramName`                — named parameter (use @Param on the method arg)
 *   - `?1, ?2`                    — positional (avoid; harder to read)
 *   - `JOIN b.author a`           — explicit join through relation
 *   - `LEFT JOIN`, `INNER JOIN`   — same as SQL
 *   - `IS NULL`, `IS NOT NULL`, `IN :list`, `BETWEEN :low AND :high`
 *   - `LIKE :pattern`             — you build the wildcards: pass "%foo%" as the param
 *   - `LOWER(b.title) LIKE LOWER(:kw)` — case-insensitive
 *   - `ORDER BY b.publishedAt DESC`
 *   - `COUNT`, `SUM`, `AVG`, `MIN`, `MAX`, `GROUP BY`, `HAVING`
 *   - `SELECT DISTINCT`
 *
 * Modifying queries:
 *   @Modifying
 *   @Query("UPDATE Book b SET b.deletedAt = CURRENT_TIMESTAMP WHERE b.id = :id")
 *   int softDelete(@Param("id") Long id);
 *
 *   Must be inside @Transactional (on service).
 *
 * Return types:
 *   - Entity     → SELECT b, returns Book / List<Book> / Optional<Book>
 *   - Projection → SELECT b.title, b.price returns Object[] or a DTO (via constructor expression):
 *                  @Query("SELECT new some.pkg.BookRow(b.title, b.price) FROM Book b")
 *
 * How to use this file:
 *   Each quiz asks for a JPQL string. Return it as-is (single line is fine).
 *   Whitespace and case are compared loosely in the printout.
 *
 * The pretend schema is in ../repository/SampleDomain.java.
 */
public class JpqlPractice {

    public static void main(String[] args) {
        System.out.println("=== JpqlPractice ===\n");
        print("Q1",  quiz1(),
                "SELECT b FROM Book b WHERE b.title = :title");
        print("Q2",  quiz2(),
                "SELECT b FROM Book b WHERE b.author.name = :name");
        print("Q3",  quiz3(),
                "SELECT b FROM Book b WHERE b.deletedAt IS NULL");
        print("Q4",  quiz4(),
                "SELECT b FROM Book b WHERE LOWER(b.title) LIKE LOWER(CONCAT('%', :kw, '%'))");
        print("Q5",  quiz5(),
                "SELECT b FROM Book b WHERE b.author.country = :country AND b.deletedAt IS NULL ORDER BY b.publishedAt DESC");
        print("Q6",  quiz6(),
                "SELECT COUNT(b) FROM Book b WHERE b.author.id = :authorId");
        print("Q7",  quiz7(),
                "SELECT DISTINCT b.author FROM Book b WHERE b.price > :min");
        print("Q8",  quiz8(),
                "SELECT b FROM Book b JOIN b.author a WHERE a.country IN :countries");
        print("Q9",  quiz9(),
                "SELECT a, COUNT(b) FROM Author a LEFT JOIN Book b ON b.author = a GROUP BY a");
        print("Q10", quiz10(),
                "SELECT b FROM Book b WHERE b.publishedAt BETWEEN :from AND :to");
    }

    private static void print(String label, String actual, String expected) {
        System.out.println("--- " + label + " ---");
        System.out.println("  yours:    " + actual);
        System.out.println("  expected: " + expected);
        System.out.println();
    }

    // ---------------- QUIZZES ----------------

    /** Q1: Return JPQL to find all Books whose title EQUALS :title */
    static String quiz1() {
        // TODO
        return "";
    }

    /** Q2: Return JPQL to find all Books written by an author whose name is :name.
     *  Navigate b.author.name. */
    static String quiz2() {
        // TODO
        return "";
    }

    /** Q3: Return JPQL for all Books where deletedAt IS NULL (not soft-deleted). */
    static String quiz3() {
        // TODO
        return "";
    }

    /** Q4: Return JPQL for a case-insensitive title CONTAINS search.
     *  Hint: LOWER(...) + LIKE + CONCAT('%', :kw, '%') */
    static String quiz4() {
        // TODO
        return "";
    }

    /** Q5: Return JPQL: all non-deleted Books by authors from :country, ordered newest first. */
    static String quiz5() {
        // TODO
        return "";
    }

    /** Q6: Return JPQL that COUNTS the books for a given authorId.
     *  Return type in repo would be `long`. */
    static String quiz6() {
        // TODO
        return "";
    }

    /** Q7: Return JPQL for the distinct authors who have written at least one book priced above :min. */
    static String quiz7() {
        // TODO
        return "";
    }

    /** Q8: Return JPQL using an explicit JOIN + IN clause on a collection param :countries. */
    static String quiz8() {
        // TODO
        return "";
    }

    /** Q9: Return JPQL to get each Author with their book count (may be zero → LEFT JOIN).
     *  Result rows are Object[] {Author, Long}. */
    static String quiz9() {
        // TODO
        return "";
    }

    /** Q10: Return JPQL for books published between :from and :to (inclusive on both). */
    static String quiz10() {
        // TODO
        return "";
    }
}
