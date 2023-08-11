package com.example.demo.User;

import com.example.demo.Volunteer.Volunteer;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Class representing data of user regarding his credentials and account status
 *
 * @author Thorvas
 */
@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users")
public class UserData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username", unique = true)
    private String username;

    @Column(name = "password")
    private String password;

    @Column(name = "is_active")
    private boolean isActive;

    @Column(name = "is_unlocked")
    private boolean isAccountNonLocked;

    @Column(name = "is_actual")
    private boolean isAccountNonExpired;

    @Column(name = "credentials_actual")
    private boolean isCredentialsNonExpired;

    @Column(name = "is_enabled")
    private boolean isEnabled;

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private UserRole role;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "volunteer_id")
    private Volunteer referencedVolunteer;

    public boolean isAdmin() {

        return role.equals(UserRole.ROLE_ADMIN);
    }

}
