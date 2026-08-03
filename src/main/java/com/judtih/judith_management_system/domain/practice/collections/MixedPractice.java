package com.judtih.judith_management_system.domain.practice.collections;

import java.util.*;

/**
 * MIXED PRACTICE — real-ish scenarios that combine Set, List, Map, and Collections methods.
 * These mirror patterns you'll write in BoardService / AnnouncementService / DashboardService.
 *
 * A tiny shared toy domain:
 *   Role  — enum standing in for UserRole (LEADER, ACTOR, STAGE, SOUND)
 *   Post  — record with (id, department, authorName)
 *
 * Nothing here is used by Spring — pure practice.
 */
public class MixedPractice {

    enum Role { LEADER, ACTOR, STAGE, SOUND, IMAGE }

    /** Full-access roles — leaders can post anywhere. */
    static final Set<Role> FULL_ACCESS = Set.of(Role.LEADER);

    record Post(long id, String department, String author) {}

    public static void main(String[] args) {
        System.out.println("=== MixedPractice ===\n");

        // Quiz 1
        System.out.println("Quiz 1a: " + canPost(Set.of(Role.ACTOR), Set.of(Role.STAGE)));    // false
        System.out.println("Quiz 1b: " + canPost(Set.of(Role.STAGE), Set.of(Role.STAGE)));    // true
        System.out.println("Quiz 1c: " + canPost(Set.of(Role.LEADER), Set.of(Role.STAGE)));   // true (full access)
        System.out.println("Quiz 1d: " + canPost(Set.of(Role.ACTOR), Set.of()));              // true (open board)

        // Quiz 2
        List<Post> posts = List.of(
                new Post(1, "STAGE", "kim"),
                new Post(2, "SOUND", "lee"),
                new Post(3, "STAGE", "park"),
                new Post(4, "STAGE", "kim"),
                new Post(5, "PRINT", "choi")
        );
        System.out.println("Quiz 2: " + countByDepartment(posts));
        // expected: {STAGE=3, SOUND=1, PRINT=1}

        // Quiz 3
        System.out.println("Quiz 3: " + groupByDepartment(posts));
        // expected: {STAGE=[Post[id=1..], Post[id=3..], Post[id=4..]], SOUND=[...], PRINT=[...]}

        // Quiz 4
        System.out.println("Quiz 4: " + distinctAuthors(posts));
        // expected: [kim, lee, park, choi] (order may vary — Set)

        // Quiz 5
        System.out.println("Quiz 5: " + hasAnyDuplicate(List.of(1, 2, 3, 4, 5)));    // false
        System.out.println("Quiz 5: " + hasAnyDuplicate(List.of(1, 2, 3, 2, 5)));    // true

        // Quiz 6
        System.out.println("Quiz 6: " + latestByDepartment(posts));
        // expected: {STAGE=Post[id=4..], SOUND=Post[id=2..], PRINT=Post[id=5..]}

        // Quiz 7 — the readMap pattern from AnnouncementService
        List<Post> announcements = List.of(new Post(10,"A","kim"), new Post(20,"A","lee"), new Post(30,"B","park"));
        Set<Long> readIds = Set.of(10L, 30L);
        System.out.println("Quiz 7: " + unreadOnly(announcements, readIds));
        // expected: [Post[id=20..]]

        // Quiz 8
        System.out.println("Quiz 8: " + topAuthorByCount(posts));
        // expected: kim (2 posts)
    }

    /** Quiz 1: Implement canPost — the same logic you're about to write in Department.canPost.
     *  Rules:
     *    a) memberRoles contains any FULL_ACCESS role → true
     *    b) targetRoles is empty → true (open)
     *    c) otherwise → true only if memberRoles and targetRoles share at least one element */
    static boolean canPost(Set<Role> memberRoles, Set<Role> targetRoles) {
        // TODO
        return false;
    }

    /** Quiz 2: Return Map<department, count>.
     *  Try both: manual loop with merge(), OR streams (Collectors.groupingBy + counting). */
    static Map<String, Long> countByDepartment(List<Post> posts) {
        // TODO
        return null;
    }

    /** Quiz 3: Return Map<department, List<Post>> — all posts grouped by department.
     *  Try both: manual (computeIfAbsent), OR streams (Collectors.groupingBy). */
    static Map<String, List<Post>> groupByDepartment(List<Post> posts) {
        // TODO
        return null;
    }

    /** Quiz 4: Return the set of distinct author names.
     *  Hint: one line with streams (.map + Collectors.toSet), or new HashSet<>(...). */
    static Set<String> distinctAuthors(List<Post> posts) {
        // TODO
        return null;
    }

    /** Quiz 5: Return true if the list has any duplicate value.
     *  Efficient version: put each element in a Set, return early when .add returns false. */
    static boolean hasAnyDuplicate(List<Integer> nums) {
        // TODO
        return false;
    }

    /** Quiz 6: For each department, return the post with the HIGHEST id (most recent).
     *  Return Map<department, Post>.
     *  Hint: Collectors.toMap with merge function, OR groupingBy + Collectors.maxBy. */
    static Map<String, Post> latestByDepartment(List<Post> posts) {
        // TODO
        return null;
    }

    /** Quiz 7: Return only the announcements whose id is NOT in readIds.
     *  This is the exact pattern used in AnnouncementService for isRead lookup. */
    static List<Post> unreadOnly(List<Post> announcements, Set<Long> readIds) {
        // TODO
        return null;
    }

    /** Quiz 8: Return the name of the author with the most posts.
     *  If there's a tie, any of the tied authors is fine.
     *  Hint: count first (Map<String, Long>), then find the max entry. */
    static String topAuthorByCount(List<Post> posts) {
        // TODO
        return null;
    }
}
