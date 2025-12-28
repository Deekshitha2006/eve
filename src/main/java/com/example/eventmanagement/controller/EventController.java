package com.example.eventmanagement.controller;

import com.example.eventmanagement.model.Event;
import com.example.eventmanagement.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class EventController {

    @Autowired
    private EventRepository eventRepository;

    // CHANGE THIS: Root '/' now opens the Splash Page
    @GetMapping("/")
    public String showLanding(Model model) {
        model.addAttribute("events", eventRepository.findAll());
        return "landing"; // Opens landing.html (which auto-redirects)
    }

    // NEW: Explicit Dashboard URL
    @GetMapping("/index")
    public String viewDashboard(Model model) {
        model.addAttribute("events", eventRepository.findAll());
        return "index"; // Opens dashboard
    }

    @GetMapping("/new")
    public String showNewEventForm(Model model) {
        model.addAttribute("event", new Event());
        return "new_event";
    }

    @PostMapping("/save")
    public String saveEvent(@ModelAttribute("event") Event event) {
        eventRepository.save(event);
        return "redirect:/index"; // Go to dashboard
    }

    @GetMapping("/delete/{id}")
    public String deleteEvent(@PathVariable Long id) {
        eventRepository.deleteById(id);
        return "redirect:/index";
    }
}