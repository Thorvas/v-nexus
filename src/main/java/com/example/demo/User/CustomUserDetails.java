package com.example.demo.User;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Custom implementation of user details
 */
@Getter
@Setter
public class CustomUserDetails implements UserDetails {

    private String username;
    private String password;
    private boolean isActive;
    private boolean isAccountNonLocked;
    private boolean isAccountNonExpired;
    private boolean isCredentialsNonExpired;
    private boolean isEnabled;
    private Set<GrantedAuthority> authorities;
    private UserData userData;

    public CustomUserDetails(UserData user) {
        this.setAuthorities(user.getRole().getAuthorities().stream()
                .map(authority -> new SimpleGrantedAuthority(authority.getAuthority()))
                .collect(Collectors.toSet()));
        this.setActive(user.isActive());
        this.setPassword(user.getPassword());
        this.setUsername(user.getUsername());
        this.setEnabled(user.isEnabled());
        this.setAccountNonExpired(user.isAccountNonExpired());
        this.setAccountNonLocked(user.isAccountNonLocked());
        this.setCredentialsNonExpired(user.isCredentialsNonExpired());
        this.setUserData(user);
    }
}
