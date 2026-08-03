package com.judtih.judith_management_system.domain.practice.sql;

/**
 * NATIVE SQL PRACTICE (@Query with nativeQuery = true)
 * =====================================================
 *
 * Sometimes JPQL isn't enough — DB-specific features, window functions, advanced JOINs,
 * pure performance tuning. Then you drop to native SQL.
 *
 * Key differences from JPQL:
 *   - Uses TABLE names and COLUMN names (SQL-level), NOT entity/field names
 *   - No property navigation (b.author.name) — you have to JOIN manually on FK columns
 *   - Portability trade-off: MySQL syntax works on prod but H2 may differ in edge cases
 *
 * Shape:
 *   @Query(value = "SELECT * FROM book WHERE title = :title", nativeQuery = true)
 *   List<Book> findByTitleNative(@Param("title") String title);
 *
 * When to use native over JPQL:
 *   1. You need a feature JPQL doesn't have (window fn, CTE, DB-specific JSON ops)
 *   2. Complex CASE expressions
 *   3. UNION queries
 *   4. Bulk-update or bulk-delete for speed (still needs @Modifying + @Transactional)
 *
 * Naming convention (default Hibernate strategy → snake_case):
 *   Entity Book.publishedAt → column published_at
 *   Entity Author (FK on Book) → column author_id on the book table
 *   Verify with H2 console / MySQL client if unsure.
 *
 * Return types:
 *   - Full entity: SELECT * FROM book → List<Book> works if the result columns match Book's mapping
 *   - Partial or aggregate: use Object[] or a projection (interface-based projection recommended)
 *   - Modifying: int / void
 *
 * Gotchas:
 *   - :param binding still works with named params
 *   - IN (:list) → the JDBC driver handles list expansion; make sure list is non-empty
 *   - LIKE with wildcards: pass '%foo%' from Java, don't try to CONCAT in native SQL for portability
 *   - MySQL is case-insensitive on default collation; H2 is case-sensitive by default
 *
 * How to use this file:
 *   Return the SQL string. Assume table names in snake_case matching the entity mapping
 *   in SampleDomain.java (book, author, library, library_book_stock).
 */
public class NativeSqlPractice {

    public static void main(String[] args) {
        System.out.println("=== NativeSqlPractice ===\n");
        print("Q1", quiz1(),
                "SELECT * FROM book WHERE title = :title");
        print("Q2", quiz2(),
                "SELECT * FROM book WHERE deleted_at IS NULL");
        print("Q3", quiz3(),
                "SELECT b.* FROM book b JOIN author a ON b.author_id = a.id WHERE a.country = :country");
        print("Q4", quiz4(),
                "UPDATE book SET deleted_at = NOW() WHERE id = :id");
        print("Q5", quiz5(),
                "SELECT COUNT(*) FROM book WHERE author_id = :authorId");
        print("Q6", quiz6(),
                "SELECT * FROM book WHERE LOWER(title) LIKE LOWER(:kw)");
        print("Q7", quiz7(),
                "SELECT a.id, a.name, COUNT(b.id) FROM author a LEFT JOIN book b ON b.author_id = a.id GROUP BY a.id, a.name");
        print("Q8", quiz8(),
                "SELECT * FROM book WHERE published_at BETWEEN :from AND :to ORDER BY published_at DESC");
        print("Q9", quiz9(),
                "DELETE FROM book WHERE author_id = :authorId");
        print("Q10", quiz10(),
                "SELECT DISTINCT a.* FROM author a JOIN book b ON b.author_id = a.id WHERE b.price > :min");
    }

    private static void print(String label, String actual, String expected) {
        System.out.println("--- " + label + " ---");
        System.out.println("  yours:    " + actual);
        System.out.println("  expected: " + expected);
        System.out.println();
    }

    // ---------------- QUIZZES ----------------

    /** Q1: Native SQL for all book rows where title = :title */
    static String quiz1() {
        // TODO
        return "";
    }

    /** Q2: Native SQL for all book rows where deleted_at IS NULL */
    static String quiz2() {
        // TODO
        return "";
    }

    /** Q3: Native SQL: JOIN book with author, filter by author.country = :country. Return all book columns. */
    static String quiz3() {
        // TODO
        return "";
    }

    /** Q4: Native UPDATE — soft-delete a book by id (set deleted_at to now).
     *  Include @Modifying on the repo method (not part of the string here). */
    static String quiz4() {
        // TODO
        return "";
    }

    /** Q5: Native SQL to COUNT books by an authorId. */
    static String quiz5() {
        // TODO
        return "";
    }

    /** Q6: Native SQL for case-insensitive LIKE on title. Assume the caller passes '%foo%'. */
    static String quiz6() {
        // TODO
        return "";
    }

    /** Q7: Native SQL: for each author, return (id, name, book_count). Even zero-book authors.
     *  Result rows are Object[] {Long, String, Long}. */
    static String quiz7() {
        // TODO
        return "";
    }

    /** Q8: Native SQL: books published between :from and :to, most recent first. */
    static String quiz8() {
        // TODO
        return "";
    }

    /** Q9: Native DELETE — remove all books by an authorId. */
    static String quiz9() {
        // TODO
        return "";
    }

    /** Q10: Native SQL: distinct authors who have at least one book priced above :min. */
    static String quiz10() {
        // TODO
        return "";
    }
}
