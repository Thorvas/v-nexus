package com.example.demo.DummyObject;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "authorities")
public class Authority {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String authority;

    @ManyToOne
    @JoinColumn(name = "custom_user_id", nullable = false)
    private Volunteer user;
}
