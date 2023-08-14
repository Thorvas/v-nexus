package com.example.demo.Project;

import com.example.demo.Category.Category;
import com.example.demo.Error.EntityNotPresentInCollectionException;
import com.example.demo.Error.EntityPresentInCollectionException;
import com.example.demo.Request.VolunteerRequest;
import com.example.demo.Opinion.Opinion;
import com.example.demo.Utility.CollectionsUtil;
import com.example.demo.Volunteer.Volunteer;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;
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
    private List<Volunteer> projectVolunteers;

    @ManyToOne
    @JoinColumn(name = "volunteer_owner")
    private Volunteer ownerVolunteer;

    @Column(name = "project_location")
    private String projectLocation;

    @Column(name = "project_status")
    private boolean projectStatus;

    @OneToMany(mappedBy = "describedProject")
    private List<Opinion> projectOpinions;

    @OneToMany(mappedBy = "requestedProject")
    private Set<VolunteerRequest> requestsToProject;

    @ManyToMany
    @JoinTable(
            name = "category_project",
            joinColumns = @JoinColumn(name = "project_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id"))
    private List<Category> categories;

    public List<Volunteer> addVolunteerToProject(Volunteer volunteer) {

        if (!this.getProjectVolunteers().contains(volunteer)) {

            List<Volunteer> newList = CollectionsUtil.addElementToList(this.getProjectVolunteers(), volunteer);
            this.setProjectVolunteers(newList);

            return newList;
        }

        throw new EntityPresentInCollectionException("Volunteer is already in project.");
    }

    public List<Volunteer> removeVolunteerFromProject(Volunteer volunteer) {

        if (this.getProjectVolunteers().contains(volunteer)) {

            List<Volunteer> newList = CollectionsUtil.removeElementFromList(this.getProjectVolunteers(), volunteer);
            this.setProjectVolunteers(newList);

            return newList;
        }

        throw new EntityNotPresentInCollectionException("Volunteer does not belong to this project.");
    }

    public List<Category> addCategoryToProject(Category category) {

        if (!this.getCategories().contains(category)) {

            List<Category> newList = CollectionsUtil.addElementToList(this.getCategories(), category);
            this.setCategories(newList);

            return newList;
        }

        throw new EntityPresentInCollectionException("Project is already assigned to this category.");
    }

    public List<Category> removeCategoryFromProject(Category category) {

        if (this.getCategories().contains(category)) {
            List<Category> newList = CollectionsUtil.removeElementFromList(this.getCategories(), category);
            this.setCategories(newList);

            return newList;
        }

        throw new EntityNotPresentInCollectionException("Project is not assigned to this category.");
    }
}
