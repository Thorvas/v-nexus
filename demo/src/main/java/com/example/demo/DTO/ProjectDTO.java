package com.example.demo.DTO;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Data;

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

    private List<VolunteerDTO> projectVolunteers;
    private VolunteerDTO projectOwner;
    private String projectLocation;
    private List<String> requiredSkills;
    private List<OpinionDTO> projectOpinions;
    private boolean projectStatus;
    private List<String> tasks;
}
