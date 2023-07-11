package com.example.demo.DummyObject;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "opinion")
public class Opinion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "opinion_content")
    private String opinion;

    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project describedProject;

    @OneToOne
    @JoinColumn(name = "volunteer_id")
    private Volunteer author;

}
