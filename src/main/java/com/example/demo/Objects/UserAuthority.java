package com.example.demo.Objects;

/**
 * Class representing authorities stored within roles
 *
 * @author Thorvas
 */
public enum UserAuthority {
    READ("AUTHORITY_READ"),
    WRITE("AUTHORITY_WRITE"),
    ADMIN("AUTHORITY_ADMIN");

    private final String authority;

    UserAuthority(String authority) {
        this.authority = authority;
    }

    public String getAuthority() {

        return this.authority;
    }
}
