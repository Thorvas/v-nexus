package com.example.demo.DummyObject;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;


@Data
@Entity
@Table(name = "project")
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "project_name")
    private String projectName;

    @Column(name = "project_description")
    private String projectDesription;

    @Column(name = "project_date")
    private LocalDate projectDate;

    @OneToMany(mappedBy = "project")
    private Set<Volunteer> projectVolunteers;

    @Column(name = "project_location")
    private String projectLocation;

    @ElementCollection
    private List<String> requiredSkills;

    @Column(name = "project_status")
    private String projectStatus;

    @OneToMany(mappedBy = "describedProject")
    private List<Opinion> projectOpinions;

    @ElementCollection
    private List<String> tasks;

}
