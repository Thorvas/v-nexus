package com.example.demo.Opinion;

import com.example.demo.Project.Project;
import com.example.demo.Volunteer.Volunteer;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Class representing opinions of projects
 */
@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "opinion")
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id"
)
public class Opinion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "opinion_content")
    private String opinion;

    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project describedProject;

    @ManyToOne
    @JoinColumn(name = "volunteer_id")
    private Volunteer author;

}
