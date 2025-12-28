package com.example.eventmanagement.repository;

import com.example.eventmanagement.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    // --- FIX: ADD THIS METHOD --- //
    boolean existsByUserIdAndEventId(Long userId, Long eventId);
    // ----------------------------------- //
}