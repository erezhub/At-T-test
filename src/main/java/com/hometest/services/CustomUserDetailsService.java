package com.hometest.services;

import com.hometest.database.entities.UserEntity;
import com.hometest.database.repositories.UserRepository;
import com.hometest.dto.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Fetch user from database
        Optional<UserEntity> user = Optional.ofNullable(userRepository.findByUserName(username));

        if (user.isEmpty()) {
            throw new UsernameNotFoundException("User not found: " + username);
        }

        // Return CustomUserDetails for the fetched user
        return new CustomUserDetails(user.get());
    }
}