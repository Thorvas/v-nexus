    package com.example.demo.Objects;

    import com.fasterxml.jackson.annotation.JsonFormat;
    import com.fasterxml.jackson.annotation.JsonIdentityInfo;
    import com.fasterxml.jackson.annotation.ObjectIdGenerators;
    import jakarta.persistence.*;
    import lombok.*;

    import java.time.LocalDate;
    import java.util.ArrayList;
    import java.util.HashSet;
    import java.util.List;
    import java.util.Set;


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

        @ElementCollection
        private List<String> requiredSkills;

        @Column(name = "project_status")
        private boolean projectStatus;

        @OneToMany(mappedBy = "describedProject")
        private List<Opinion> projectOpinions;

        @OneToMany(mappedBy = "requestedProject")
        private Set<VolunteerRequest> requestsToProject;

        @ElementCollection
        private List<String> tasks;


        @ManyToMany
        @JoinTable(
                name = "category_project",
                joinColumns = @JoinColumn(name = "project_id"),
                inverseJoinColumns = @JoinColumn(name = "category_id"))
        private List<Category> categories;

        public Set<Volunteer> addVolunteerToProject(Volunteer volunteer) {

            Set<Volunteer> newList = this.getProjectVolunteers();

            if (newList == null) {

                newList = new HashSet<>();
            }
            newList.add(volunteer);
            this.setProjectVolunteers(newList);

            return newList;
        }

        public Set<Volunteer> removeVolunteerFromProject(Volunteer volunteer) {

            Set<Volunteer> newList = this.getProjectVolunteers();

            if (newList != null) {

                if (newList.contains(volunteer)) {

                    newList.remove(volunteer);
                    this.setProjectVolunteers(newList);

                    return newList;

                } else {

                    throw new EntityNotFoundException("Such volunteer does not belong to this project.");
                }
            } else {

                throw new RuntimeException("There are no participants in this project.");
            }
        }

        public List<Opinion> addOpinionToProject(Opinion opinion) {

            List<Opinion> newList = this.getProjectOpinions();

            if (newList == null) {

                newList = new ArrayList<>();

            }
            newList.add(opinion);
            this.setProjectOpinions(newList);

            return newList;
        }

        public List<Opinion> removeOpinionFromProject(Opinion opinion) {

            List<Opinion> newList = this.getProjectOpinions();

            if (newList != null) {

                if (newList.contains(opinion)) {

                    newList.remove(opinion);
                    this.setProjectOpinions(newList);

                    return newList;
                }
                else {
                    throw new EntityNotFoundException("Such opinion does not belong to this project.");
                }
            } else {
                throw new RuntimeException("There are no opinions in this project.");
            }
        }
    }
