package com.codavert.config;

import com.codavert.entity.User;
import com.codavert.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Check if admin user already exists (by username or email)
        if (userRepository.findByUsername("admin").isEmpty() && 
            userRepository.findByEmail("admin@codavert.com").isEmpty()) {
            
            User adminUser = new User();
            adminUser.setUsername("admin");
            adminUser.setEmail("admin@codavert.com");
            adminUser.setPassword(passwordEncoder.encode("admin123"));
            adminUser.setFirstName("Admin");
            adminUser.setLastName("User");
            adminUser.setRole(User.Role.ADMIN);
            adminUser.setStatus(User.UserStatus.ACTIVE);
            
            userRepository.save(adminUser);
            System.out.println("Default admin user created: admin/admin123");
        } else {
            System.out.println("Admin user already exists, skipping creation");
        }
    }
}
