package com.example.eventmanagement.controller;

import com.example.eventmanagement.repository.BookingRepository;
import com.example.eventmanagement.repository.EventRepository;
import com.example.eventmanagement.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AdminController {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // Access this URL to reset everything: http://localhost:8081/admin/reset
    @GetMapping("/admin/reset")
    public String resetDatabase(RedirectAttributes redirectAttributes) {
        try {
            // Step 1: Delete all data
            bookingRepository.deleteAll();
            eventRepository.deleteAll();
            // Don't delete users - keep user ID 1

            // Step 2: Reset ID counters
            jdbcTemplate.execute("ALTER TABLE event ALTER COLUMN id RESTART WITH 1");
            jdbcTemplate.execute("ALTER TABLE booking ALTER COLUMN id RESTART WITH 1");

            redirectAttributes.addFlashAttribute("success", "✅ Database Reset! IDs start from 1 again.");
            return "redirect:/index";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Reset failed: " + e.getMessage());
            return "redirect:/index";
        }
    }
}