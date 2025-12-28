package com.example.eventmanagement.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate bookingDate;

    // --- USER RELATIONSHIP --- //
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    // --- EVENT RELATIONSHIP --- //
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Event event;
    // ------------------------------------------------ //

    // --- STATUS FIELD (Stored in database) --- //
    @Column(name = "is_expired", nullable = false)
    private boolean isExpired = false;
    // ---------------------------------------- //

    public Booking() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDate getBookingDate() { return bookingDate; }
    public void setBookingDate(LocalDate bookingDate) { this.bookingDate = bookingDate; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Event getEvent() { return event; }
    public void setEvent(Event event) { this.event = event; }

    // --- GETTERS AND SETTERS FOR STATUS --- //
    public boolean isExpired() {
        return isExpired;
    }

    public void setExpired(boolean isExpired) {
        this.isExpired = isExpired;
    }
    // ------------------------------------------------------- //

    // --- SAFE EVENT GETTER (Prevents Ghost Event Crash) --- //
    public Event getEventSafe() {
        if (this.event != null) {
            return this.event;
        }
        return null; // Returns null if event is missing/ghost
    }
    // ------------------------------------------------ //
}