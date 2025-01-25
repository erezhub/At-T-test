package com.hometest.services;

import com.hometest.database.entities.UserEntity;
import com.hometest.database.repositories.UserRepository;
import com.hometest.dto.UserDto;
import com.hometest.enums.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${admin.name}")
    private String adminName;
    @Value("${admin.password}")
    private String adminPassword;

    @PostConstruct
    public void init() {
        UserEntity userEntity = UserEntity.builder()
                .role(UserRole.ROLE_ADMIN)
                .userName(adminName)
                .password(passwordEncoder.encode(adminPassword)).build();
        userRepository.save(userEntity);
    }

    /**
     * creates a new user
     * @param dto - user's details
     * @return - created user-id
     */
    public long createUser(UserDto dto) {
        UserEntity userEntity = UserEntity.builder()
                .userName(dto.getName())
                .password(passwordEncoder.encode(dto.getPassword()))
                .email(dto.getEmail())
                .role(UserRole.valueOf(dto.getRole()))
                .build();
        UserEntity saved = userRepository.save(userEntity);
        return saved.getId();
    }
}
