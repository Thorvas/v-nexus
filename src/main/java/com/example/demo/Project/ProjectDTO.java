package com.example.demo.Project;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import java.time.LocalDate;

/**
 * Data Transfer Object for projects
 *
 * @author Thorvas
 */
@Data
@Relation(collectionRelation = "projects", itemRelation = "project")
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id"
)
public class ProjectDTO extends RepresentationModel<ProjectDTO> {

    private Long id;

    @Size(min = 4, max = 10, message = "Project name must be at least 4 or max 10 characters long.")
    @NotBlank(message = "Project name cannot be empty.")
    @JsonProperty(value = "name")
    private String projectName;

    @Size(min = 4, message = "Project description must be at least 4 characters long.")
    @NotBlank(message = "Project description cannot be empty.")
    @JsonProperty(value = "description")
    private String projectDescription;

    @FutureOrPresent(message = "Project date must include future or present date.")
    @NotNull(message = "Project date cannot be empty.")
    @JsonProperty(value = "date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate projectDate;

    @Size(min = 3, message = "Project location must be at least 3 characters long.")
    @NotBlank(message = "Project location cannot be empty.")
    @JsonProperty(value = "location")
    private String projectLocation;

    @JsonProperty(value = "isActive")
    @NotNull(message = "Project status should either be 'true' or 'false'")
    private Boolean projectStatus;
}
