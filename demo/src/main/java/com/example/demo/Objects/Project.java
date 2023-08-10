package com.example.demo.Objects;

import com.example.demo.Utility.CollectionsUtil;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Set;


/**
 * Class representing project
 *
 * @author Thorvas
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
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
    private Set<Volunteer> projectVolunteers;

    @ManyToOne
    @JoinColumn(name = "volunteer_owner")
    private Volunteer ownerVolunteer;

    @Column(name = "project_location")
    private String projectLocation;

    @Column(name = "project_status")
    private boolean projectStatus;

    @OneToMany(mappedBy = "describedProject")
    private Set<Opinion> projectOpinions;

    @OneToMany(mappedBy = "requestedProject")
    private Set<VolunteerRequest> requestsToProject;

    @ManyToMany
    @JoinTable(
            name = "category_project",
            joinColumns = @JoinColumn(name = "project_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id"))
    private Set<Category> categories;

    public Set<Volunteer> addVolunteerToProject(Volunteer volunteer) {

        Set<Volunteer> newList = CollectionsUtil.addElementToSet(this.getProjectVolunteers(), volunteer);
        this.setProjectVolunteers(newList);

        return newList;
    }

    public Set<Volunteer> removeVolunteerFromProject(Volunteer volunteer) {

        Set<Volunteer> newList = CollectionsUtil.removeElementFromSet(this.getProjectVolunteers(), volunteer);
        this.setProjectVolunteers(newList);

        return newList;
    }

    public Set<Category> addCategoryToProject(Category category) {

        Set<Category> newList = CollectionsUtil.addElementToSet(this.getCategories(), category);
        this.setCategories(newList);

        return newList;
    }

    public Set<Category> removeCategoryFromProject(Category category) {

        Set<Category> newList = CollectionsUtil.removeElementFromSet(this.getCategories(), category);
        this.setCategories(newList);

        return newList;
    }
}
