package com.example.demo.Objects;

import java.util.Arrays;
import java.util.List;

public enum UserRole {
    ROLE_VOLUNTEER(Arrays.asList(UserAuthority.READ, UserAuthority.WRITE)),
    ROLE_ADMIN(Arrays.asList(UserAuthority.READ, UserAuthority.WRITE, UserAuthority.ADMIN));

    private final List<UserAuthority> authorities;

    UserRole(List<UserAuthority> authorities) {

        this.authorities = authorities;
    }

    public List<UserAuthority> getAuthorities() {

        return this.authorities;
    }
}
