package com.example.demo.Objects;

import java.util.Arrays;
import java.util.List;

public enum UserRole {
    ROLE_VOLUNTEER(Arrays.asList(UserAuthority.READ, UserAuthority.WRITE)),
    ROLE_ADMIN(Arrays.asList(UserAuthority.READ, UserAuthority.WRITE, UserAuthority.ADMIN));

    private final List<UserAuthority> roles;

    UserRole(List<UserAuthority> roles) {

        this.roles = roles;
    }

    public List<UserAuthority> getRoles() {

        return this.roles;
    }
}
