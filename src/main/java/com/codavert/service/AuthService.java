package com.codavert.service;

import com.codavert.dto.JwtResponseDto;
import com.codavert.dto.LoginRequestDto;
import com.codavert.dto.UserUpdateDto;
import com.codavert.dto.UserRegistrationDto;
import com.codavert.entity.User;
import com.codavert.repository.UserRepository;
import com.codavert.security.JwtUtils;
import com.codavert.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class AuthService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private AuthenticationManager authenticationManager;
    
    @Autowired
    private JwtUtils jwtUtils;
    
    public User registerUser(UserRegistrationDto registrationDto) {
        if (userRepository.existsByUsername(registrationDto.getUsername())) {
            throw new RuntimeException("Error: Username is already taken!");
        }
        
        if (userRepository.existsByEmail(registrationDto.getEmail())) {
            throw new RuntimeException("Error: Email is already in use!");
        }
        
        User user = new User();
        user.setUsername(registrationDto.getUsername());
        user.setEmail(registrationDto.getEmail());
        user.setPassword(passwordEncoder.encode(registrationDto.getPassword()));
        user.setFirstName(registrationDto.getFirstName());
        user.setLastName(registrationDto.getLastName());
        user.setPhone(registrationDto.getPhone());
        user.setBio(registrationDto.getBio());
        user.setRole(User.Role.USER);
        user.setStatus(User.UserStatus.ACTIVE);
        
        return userRepository.save(user);
    }
    
    public JwtResponseDto authenticateUser(LoginRequestDto loginRequest) {
        try {
            // First check if user exists and is active before attempting authentication
            User user = userRepository.findByUsername(loginRequest.getUsername())
                .orElse(null);
            
            // Also try to find by email if not found by username
            if (user == null) {
                user = userRepository.findByEmail(loginRequest.getUsername())
                    .orElse(null);
            }
            
            if (user == null) {
                throw new RuntimeException("Invalid username or password");
            }
            
            // Check if user account is active
            if (user.getStatus() != User.UserStatus.ACTIVE) {
                throw new RuntimeException("Your account has been deactivated. Please contact administrator.");
            }
            
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
            );
            
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtUtils.generateJwtToken(authentication);
            
            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            User authenticatedUser = userRepository.findByUsername(userPrincipal.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
            
            // Double-check status after authentication
            if (authenticatedUser.getStatus() != User.UserStatus.ACTIVE) {
                throw new RuntimeException("Your account has been deactivated. Please contact administrator.");
            }
            
            LocalDateTime expiresAt = LocalDateTime.now().plusDays(1); // JWT expires in 1 day
            
            return new JwtResponseDto(
                jwt,
                authenticatedUser.getId(),
                authenticatedUser.getUsername(),
                authenticatedUser.getEmail(),
                authenticatedUser.getFirstName(),
                authenticatedUser.getLastName(),
                authenticatedUser.getRole().name(),
                expiresAt
            );
        } catch (org.springframework.security.authentication.DisabledException e) {
            throw new RuntimeException("Your account has been deactivated. Please contact administrator.");
        } catch (org.springframework.security.authentication.BadCredentialsException e) {
            throw new RuntimeException("Invalid username or password");
        } catch (Exception e) {
            System.err.println("Authentication error: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Authentication failed: " + e.getMessage());
        }
    }
    
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public String getUsernameFromToken(String token) {
        return jwtUtils.getUserNameFromJwtToken(token);
    }
    
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    
    public User findById(Long id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }
    
    public User updateUserProfile(Long id, UserUpdateDto updateDto) {
        User user = findById(id);
        
        // Update basic fields
        if (updateDto.getUsername() != null && !updateDto.getUsername().isEmpty()) {
            user.setUsername(updateDto.getUsername());
        }
        if (updateDto.getEmail() != null && !updateDto.getEmail().isEmpty()) {
            user.setEmail(updateDto.getEmail());
        }
        if (updateDto.getPassword() != null && !updateDto.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(updateDto.getPassword()));
        }
        if (updateDto.getFirstName() != null && !updateDto.getFirstName().isEmpty()) {
            user.setFirstName(updateDto.getFirstName());
        }
        if (updateDto.getLastName() != null && !updateDto.getLastName().isEmpty()) {
            user.setLastName(updateDto.getLastName());
        }
        if (updateDto.getPhone() != null) {
            user.setPhone(updateDto.getPhone());
        }
        if (updateDto.getBio() != null) {
            user.setBio(updateDto.getBio());
        }
        if (updateDto.getProfileImageUrl() != null) {
            user.setProfileImageUrl(updateDto.getProfileImageUrl());
        }
        
        return userRepository.save(user);
    }
    
    public User updateUser(Long id, UserRegistrationDto updateDto) {
        User user = findById(id);
        
        if (updateDto.getFirstName() != null) {
            user.setFirstName(updateDto.getFirstName());
        }
        if (updateDto.getLastName() != null) {
            user.setLastName(updateDto.getLastName());
        }
        if (updateDto.getPhone() != null) {
            user.setPhone(updateDto.getPhone());
        }
        if (updateDto.getBio() != null) {
            user.setBio(updateDto.getBio());
        }
        
        return userRepository.save(user);
    }
    
    public void deleteUser(Long id) {
        User user = findById(id);
        user.setStatus(User.UserStatus.INACTIVE);
        userRepository.save(user);
    }
}
