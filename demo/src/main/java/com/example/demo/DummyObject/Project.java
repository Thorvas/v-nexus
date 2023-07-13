package com.example.demo.DummyObject;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;


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
    @Temporal(TemporalType.DATE)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate projectDate;

    @ManyToMany(mappedBy = "participatingProjects")
    private List<Volunteer> projectVolunteers;

    @ManyToOne
    @JoinColumn(name = "volunteer_owner")
    private Volunteer ownerVolunteer;

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
