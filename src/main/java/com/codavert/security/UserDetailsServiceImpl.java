package com.codavert.security;

import com.codavert.entity.User;
import com.codavert.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Try to find user by username first
        User user = userRepository.findByUsername(username).orElse(null);
        
        // If not found by username, try to find by email
        if (user == null) {
            user = userRepository.findByEmail(username).orElse(null);
        }
        
        if (user == null) {
            throw new UsernameNotFoundException("User Not Found with username/email: " + username);
        }
        
        // Check if user account is active
        if (user.getStatus() != User.UserStatus.ACTIVE) {
            throw new org.springframework.security.authentication.DisabledException("User account is deactivated");
        }
        
        return UserPrincipal.create(user);
    }
}
