package com.example.demo.User;

import java.util.Arrays;
import java.util.List;

/**
 * Class representing roles of user. They contain authorities that allow user to interact with API
 *
 * @author Thorvas
 */
public enum UserRole {
    ROLE_VOLUNTEER(Arrays.asList(UserAuthority.CATEGORY, UserAuthority.OPINION, UserAuthority.VOLUNTEER, UserAuthority.PROJECT)),
    ROLE_ADMIN(Arrays.asList(UserAuthority.CATEGORY, UserAuthority.OPINION, UserAuthority.ADMIN, UserAuthority.VOLUNTEER, UserAuthority.PROJECT));

    private final List<UserAuthority> authorities;

    UserRole(List<UserAuthority> authorities) {

        this.authorities = authorities;
    }

    public List<UserAuthority> getAuthorities() {

        return this.authorities;
    }
}
