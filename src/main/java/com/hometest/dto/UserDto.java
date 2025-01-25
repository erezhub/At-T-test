package com.hometest.dto;

import com.hometest.enums.UserRole;
import lombok.Data;

@Data
public class UserDto {
    private String name;
    private String email;
    private String password;
    private String role;
}
