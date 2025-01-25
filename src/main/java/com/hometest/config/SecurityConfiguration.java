package com.hometest.config;

import com.hometest.services.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
public class SecurityConfiguration {

    private final CustomUserDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .csrf().disable() // Disable CSRF for testing with Postman
                .authorizeHttpRequests()
                .regexMatchers("/movie").hasAuthority("ROLE_ADMIN")
                .regexMatchers("/user").hasAuthority("ROLE_ADMIN")
                .regexMatchers("/screening").hasAuthority("ROLE_ADMIN")
                .regexMatchers("/screening/title").hasAnyAuthority("ROLE_ADMIN", "ROLE_USER")
                .regexMatchers("/screening/theater").hasAnyAuthority("ROLE_ADMIN", "ROLE_CUSTOMER")
                .anyRequest().authenticated()
                .and()
                .httpBasic();
        return httpSecurity.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
