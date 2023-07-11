package com.example.demo.DummyObject;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;


@Data
public class Project {

    private String projectName;
    private String projectDesription;
    private LocalDate projectDate;
    private Set<Volunteer> projectVolunteers;
    private String projectLocation;
    private List<String> requiredSkills;
    private String projectStatus;
    private List<Opinion> projectOpinions;
    private List<String> projectTasks;

}
