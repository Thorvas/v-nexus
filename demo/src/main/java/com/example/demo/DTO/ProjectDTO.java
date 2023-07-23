package com.example.demo.DTO;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Data;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import java.time.LocalDate;
import java.util.List;

@Data
@Relation(collectionRelation = "projects", itemRelation = "project")
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id"
)
public class ProjectDTO extends RepresentationModel<ProjectDTO> {

    private Long id;
    private String projectName;
    private String projectDescription;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate projectDate;

    private String projectLocation;
    private boolean projectStatus;
    private List<String> tasks;
}
