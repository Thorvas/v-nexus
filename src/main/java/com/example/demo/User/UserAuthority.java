package com.example.demo.User;

/**
 * Class representing authorities stored within roles
 *
 * @author Thorvas
 */
public enum UserAuthority {
    CATEGORY("AUTHORITY_CATEGORY"),
    OPINION("AUTHORITY_OPINION"),
    PROJECT("AUTHORITY_PROJECT"),
    VOLUNTEER("AUTHORITY_VOLUNTEER"),
    ADMIN("AUTHORITY_ADMIN");

    private final String authority;

    UserAuthority(String authority) {
        this.authority = authority;
    }

    public String getAuthority() {

        return this.authority;
    }
}
