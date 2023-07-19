package com.example.demo.DTO;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Data;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.RepresentationModel;

import java.time.LocalDate;
import java.util.List;

@Data
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id"
)
public class ProjectDTO {

    private Long id;
    private String projectName;
    private String projectDescription;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate projectDate;

    private List<Link> projectVolunteers;
    private Link projectOwner;
    private String projectLocation;
    private List<String> requiredSkills;
    private List<Link> projectOpinions;
    private boolean projectStatus;
    private List<String> tasks;
    private List<Link> categories;
}
