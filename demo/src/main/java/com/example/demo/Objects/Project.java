package com.example.demo.Objects;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


@Data
@Entity
@Builder
@Table(name = "project")
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id"
)
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "project_name")
    private String projectName;

    @Column(name = "project_description")
    private String projectDescription;

    @Column(name = "project_date")
    @Temporal(TemporalType.DATE)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate projectDate;

    @ManyToMany
    @JoinTable(
            name = "volunteer_project",
            joinColumns = @JoinColumn(name = "project_id"),
            inverseJoinColumns = @JoinColumn(name = "volunteer_id"))
    private List<Volunteer> projectVolunteers;

    @ManyToOne
    @JoinColumn(name = "volunteer_owner")
    private Volunteer ownerVolunteer;

    @Column(name = "project_location")
    private String projectLocation;

    @ElementCollection
    private List<String> requiredSkills;

    @Column(name = "project_status")
    private boolean projectStatus;

    @OneToMany(mappedBy = "describedProject")
    private List<Opinion> projectOpinions;

    @ElementCollection
    private List<String> tasks;


    @ManyToMany
    @JoinTable(
            name = "category_project",
            joinColumns = @JoinColumn(name = "project_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id"))
    private List<Category> categories;

    public List<Volunteer> addVolunteerToProject(Volunteer volunteer) {

        List<Volunteer> newList = this.getProjectVolunteers();

        if (this.getProjectVolunteers() == null) {

            newList = new ArrayList<>();
            newList.add(volunteer);
            this.setProjectVolunteers(newList);
        }
        else {
            newList.add(volunteer);
        }

        return newList;
    }

    public List<Volunteer> removeVolunteerFromProject(Volunteer volunteer) {

        List<Volunteer> newList = this.getProjectVolunteers();

        if (newList != null) {

            if (newList.contains(volunteer)) {
                newList.remove(volunteer);

                return newList;
            }
            else {
                throw new EntityNotFoundException("Such volunteer does not belong to this project.");
            }
        }
        else {
            throw new RuntimeException("There are no participants in this project.");
        }
    }

}
