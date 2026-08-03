package com.judtih.judith_management_system.domain.practice.spring.library.entity;

import java.time.LocalDateTime;

/**
 * Waitlist entry — a member wants to borrow a Book (not a specific copy) that isn't available.
 * position = 1 means they're next in line.
 * fulfilled = true once a copy comes back and gets held for them.
 */
public class Reservation {

    private Long id;
    private Member member;
    private Book book;
    private int position;              // 1 = next in line
    private boolean fulfilled;
    private LocalDateTime reservedAt;
    private LocalDateTime cancelledAt; // nullable

    public Reservation() {}
    public Reservation(Member member, Book book, int position) {
        this.member = member;
        this.book = book;
        this.position = position;
        this.fulfilled = false;
        this.reservedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public Member getMember() { return member; }
    public Book getBook() { return book; }
    public int getPosition() { return position; }
    public boolean isFulfilled() { return fulfilled; }
    public boolean isCancelled() { return cancelledAt != null; }
    public LocalDateTime getReservedAt() { return reservedAt; }
    public LocalDateTime getCancelledAt() { return cancelledAt; }

    public void fulfill() { this.fulfilled = true; }
    public void cancel() { this.cancelledAt = LocalDateTime.now(); }
    public void movePositionUp() { if (this.position > 1) this.position--; }
}
