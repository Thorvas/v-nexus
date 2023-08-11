package com.example.demo.DTO;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import java.time.LocalDate;
import java.util.List;

/**
 * Data Transfer Object for volunteers
 *
 * @author Thorvas
 */
@Data
@Relation(collectionRelation = "volunteers", itemRelation = "volunteer")
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id"
)
public class VolunteerDTO extends RepresentationModel<VolunteerDTO> {

    private Long id;

    @Size(min = 4, message = "Volunteer's name must be at least 4 characters long.")
    @NotBlank(message = "Volunteer's name cannot be empty.")
    @JsonProperty(value = "name")
    private String name;

    @Size(min = 4, message = "Volunteer's surname must be at least 4 characters long.")
    @NotBlank(message = "Volunteer's surname cannot be empty.")
    @JsonProperty(value = "surname")
    private String surname;

    @JsonProperty(value = "birthDate")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateOfBirth;

    @Size(min = 4, message = "Volunteer's contact must be at least 4 characters long.")
    @NotBlank(message = "Volunteer's contact cannot be empty.")
    @JsonProperty(value = "contact")
    private String contact;

    @Min(value = 0)
    @NotNull(message = "Popularity cannot be empty")
    @JsonProperty(value = "reputation")
    private Integer reputation;

    @NotBlank(message = "Interests cannot be empty.")
    @JsonProperty(value = "interests")
    private List<String> interests;

}
