package com.example.demo.Volunteer;

import com.example.demo.Opinion.Opinion;
import com.example.demo.Project.Project;
import com.example.demo.Request.VolunteerRequest;
import com.example.demo.User.UserData;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Class representing volunteer
 *
 * @author Thorvas
 */
@Getter
@Setter
@AllArgsConstructor
@Entity
@Table(name = "volunteer")
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id"
)
public class Volunteer {

    public Volunteer() {

        this.setOwnedProjects(new ArrayList<>());
        this.setInterests(new ArrayList<>());
        this.setOpinions(new ArrayList<>());
        this.setParticipatingProjects(new ArrayList<>());
        this.setReceivedRequests(new ArrayList<>());
        this.setSentRequests(new ArrayList<>());
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "surname")
    private String surname;

    @Column(name = "birth_date")
    @Temporal(TemporalType.DATE)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateOfBirth;

    @Column(name = "phone")
    private String contact;

    @ManyToMany(mappedBy = "projectVolunteers")
    private List<Project> participatingProjects;

    @OneToMany(mappedBy = "ownerVolunteer")
    private List<Project> ownedProjects;

    @Column(name = "reputation")
    private Integer reputation;

    @OneToMany(mappedBy = "requestSender")
    private List<VolunteerRequest> sentRequests;

    @OneToMany(mappedBy = "requestReceiver")
    private List<VolunteerRequest> receivedRequests;

    @ElementCollection
    private List<String> interests;

    @OneToOne(mappedBy = "referencedVolunteer")
    private UserData userData;

    @OneToMany(mappedBy = "author")
    private List<Opinion> opinions;

}
