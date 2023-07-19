package com.example.demo.DTO;

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
public class VolunteerDTO extends RepresentationModel<VolunteerDTO> {

    private Long id;
    private String name;
    private String surname;
    private LocalDate dateOfBirth;
    private String contact;
    private List<String> skills;
    private List<Link> participatingProjects;
    private List<Link> ownedProjects;
    private Integer reputation;
    private List<String> interests;

}
