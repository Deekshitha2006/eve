package com.example.eventmanagement;

import com.example.eventmanagement.model.User;
import com.example.eventmanagement.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Override
    public void run(String... args) throws Exception {
        // --- FIX: Only insert if table is empty --- //
        if (userRepository.count() == 0) {
            User user = new User("John Doe", "john@example.com");
            userRepository.save(user);
            System.out.println(">> Created Default User: John Doe");
        }
        // ------------------------------------------------ //
    }
}