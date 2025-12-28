package com.example.eventmanagement.controller;

import com.example.eventmanagement.model.Booking;
import com.example.eventmanagement.model.Event;
import com.example.eventmanagement.model.User;
import com.example.eventmanagement.repository.BookingRepository;
import com.example.eventmanagement.repository.EventRepository;
import com.example.eventmanagement.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.servlet.http.HttpSession;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Controller
public class BookingController {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private UserRepository userRepository;

    // 1. Handle clicking "Book" (The Safe Version)
    @GetMapping("/book/{eventId}")
    public String bookEvent(@PathVariable Long eventId, RedirectAttributes redirectAttributes) {
        try {
            // --- A. VERIFY USER EXISTS --- //
            User user = userRepository.findById(1L).orElse(null);
            if (user == null) {
                redirectAttributes.addFlashAttribute("error", "CRITICAL: User (ID 1) not found. Did DataLoader run?");
                return "redirect:/index";
            }
            // ------------------------------------------------ //

            // --- B. VERIFY EVENT EXISTS --- //
            Event event = eventRepository.findById(eventId).orElse(null);
            if (event == null) {
                redirectAttributes.addFlashAttribute("error", "Error: Event not found.");
                return "redirect:/index";
            }
            // ------------------------------------------------ //

            // --- C. CHECK DUPLICATE --- //
            boolean alreadyBooked = bookingRepository.existsByUserIdAndEventId(user.getId(), eventId);
            if (alreadyBooked) {
                redirectAttributes.addFlashAttribute("error", "Booking Failed: You have already booked this event!");
                return "redirect:/index";
            }
            // ------------------------------------------------ //

            // --- D. SAVE BOOKING --- //
            Booking booking = new Booking();
            booking.setEvent(event);
            booking.setUser(user);
            booking.setBookingDate(LocalDate.now());

            // Set Status (FIXED: Check for null date)
            if (event.getDate() != null && event.getDate().isBefore(LocalDate.now())) {
                booking.setExpired(true);
            } else {
                booking.setExpired(false);
            }

            bookingRepository.save(booking);

            // Send Success Message
            redirectAttributes.addFlashAttribute("success", "Booking Successful! Added to your list.");
            return "redirect:/my-bookings/1";

        } catch (Exception e) {
            // --- E. CATCH ANY CRASH --- //
            // This prevents "Internal Server Error" page
            redirectAttributes.addFlashAttribute("error", "Internal Error: " + e.getMessage());
            return "redirect:/index";
        }
    }

    // 2. Handle clicking "My Bookings" (WITH ALERTS)
    @GetMapping("/my-bookings/{userId}")
    public String viewMyBookings(@PathVariable Long userId, Model model) {
        List<Booking> allBookings = bookingRepository.findAll();

        // Filter manually to get only bookings for this user (simpler way)
        List<Booking> myBookings = allBookings.stream()
                .filter(b -> b.getEvent() != null) // Filter out Ghost Events
                .filter(b -> b.getUser().getId().equals(userId))
                .toList();

        // --- CALCULATE STATUS IN JAVA (FIXED: Check for null date) --- //
        myBookings.forEach(booking -> {
            if (booking.getEvent() != null && booking.getEvent().getDate() != null) {
                boolean isExpired = booking.getEvent().getDate().isBefore(LocalDate.now());
                booking.setExpired(isExpired);
            } else {
                // If Event is NULL or Date is NULL, mark as Expired
                booking.setExpired(true);
            }
        });
        // ------------------------------------------------ //

        // --- CHECK FOR UPCOMING EVENTS (NEW FEATURE) --- //
        StringBuilder alertMessage = new StringBuilder();
        int urgentCount = 0;
        int soonCount = 0;

        for (Booking booking : myBookings) {
            if (booking.getEvent() != null && booking.getEvent().getDate() != null) {
                LocalDate eventDate = booking.getEvent().getDate();
                LocalDate today = LocalDate.now();

                // Only check future events
                if (eventDate.isAfter(today) || eventDate.isEqual(today)) {
                    long daysUntil = ChronoUnit.DAYS.between(today, eventDate);

                    // Event is TODAY
                    if (daysUntil == 0) {
                        alertMessage.append("🔥 <strong>")
                                .append(booking.getEvent().getTitle())
                                .append("</strong> is <strong>TODAY</strong>!<br>");
                        urgentCount++;
                    }
                    // Event is within 3 days (URGENT)
                    else if (daysUntil <= 3) {
                        alertMessage.append("⚠️ <strong>")
                                .append(booking.getEvent().getTitle())
                                .append("</strong> is in <strong>")
                                .append(daysUntil)
                                .append(" day")
                                .append(daysUntil > 1 ? "s" : "")
                                .append("</strong>!<br>");
                        urgentCount++;
                    }
                    // Event is within 7 days (SOON)
                    else if (daysUntil <= 7) {
                        alertMessage.append("📅 <strong>")
                                .append(booking.getEvent().getTitle())
                                .append("</strong> is coming up in ")
                                .append(daysUntil)
                                .append(" days<br>");
                        soonCount++;
                    }
                }
            }
        }

        // Add alerts to model if there are any upcoming events
        if (urgentCount > 0 || soonCount > 0) {
            model.addAttribute("hasAlerts", true);
            model.addAttribute("alertMessage", alertMessage.toString());
            model.addAttribute("alertType", urgentCount > 0 ? "urgent" : "info");
        }
        // ------------------------------------------------ //

        model.addAttribute("bookings", myBookings);
        return "my-bookings";
    }
}