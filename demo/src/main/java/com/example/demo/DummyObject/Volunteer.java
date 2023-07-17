package com.example.demo.DummyObject;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Data
@Entity
@Table(name = "volunteer")
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id"
)
public class Volunteer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username")
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

    @OneToMany(mappedBy = "user")
    private Set<Authority> authorities;

    @Column(name = "name")
    private String name;

    @Column(name = "surname")
    private String surname;

    @Column(name = "birth_date")
    private LocalDate dateOfBirth;

    @Column(name = "phone")
    private String contact;

    @ElementCollection
    private List<String> skills;


    @ManyToMany(mappedBy = "projectVolunteers")
    private List<Project> participatingProjects;

    @OneToMany(mappedBy = "ownerVolunteer")
    private List<Project> ownedProjects;

    @Column(name = "reputation")
    private Integer reputation;

    @ElementCollection
    private List<String> interests;

}
