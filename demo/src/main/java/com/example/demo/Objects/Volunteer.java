package com.example.demo.Objects;

import com.example.demo.Utility.CollectionsUtil;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
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

    @Column(name = "name")
    @NotBlank(message = "Name is mandatory")
    private String name;

    @Column(name = "surname")
    private String surname;

    @Column(name = "birth_date")
    @Temporal(TemporalType.DATE)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateOfBirth;

    @Column(name = "phone")
    private String contact;

    @ElementCollection
    private List<String> skills;


    @ManyToMany(mappedBy = "projectVolunteers")
    private Set<Project> participatingProjects;

    @OneToMany(mappedBy = "ownerVolunteer")
    private Set<Project> ownedProjects;

    @Column(name = "reputation")
    private Integer reputation;

    @OneToMany(mappedBy = "requestSender")
    private Set<VolunteerRequest> sentRequests;

    @OneToMany(mappedBy = "requestReceiver")
    private Set<VolunteerRequest> receivedRequests;

    @ElementCollection
    private List<String> interests;

    @OneToOne(mappedBy = "referencedVolunteer")
    private UserData userData;

    public Set<VolunteerRequest> addSentRequest(VolunteerRequest request) {

        Set<VolunteerRequest> requests = this.getSentRequests();

        this.setSentRequests(CollectionsUtil.addElementToSet(requests, request));

        return requests;
    }

    public Set<Project> addProjectToOwner(Project project) {

        Set<Project> newList = this.getOwnedProjects();

        this.setOwnedProjects(CollectionsUtil.addElementToSet(newList, project));

        return newList;
    }

    public Set<Project> removeProjectOwner(Project project) {

        Set<Project> newList = this.getOwnedProjects();

        this.setOwnedProjects(CollectionsUtil.removeElementFromSet(newList, project));

        return newList;
    }

}
