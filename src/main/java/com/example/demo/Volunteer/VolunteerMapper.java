package com.example.demo.Volunteer;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * Mapper for volunteers
 *
 * @author Thorvas
 */
@Component
public class VolunteerMapper {

    @Autowired
    private ModelMapper modelMapper;

    /**
     * Method that maps given volunteer into its DTO with HATEOAS links
     *
     * @param volunteerToMap Volunteer that is to be mapped into DTO
     * @return Mapped volunteerDTO object
     */
    public VolunteerDTO mapVolunteerToDTO(Volunteer volunteerToMap) {

        VolunteerDTO newDTO = modelMapper.map(volunteerToMap, VolunteerDTO.class);

        Link allProjectsLink = linkTo(methodOn(VolunteerController.class)
                .getProjects(volunteerToMap.getId())).withRel("participated-projects");

        Link allOwnedProjectsLink = linkTo(methodOn(VolunteerController.class)
                .getOwnedProjects(volunteerToMap.getId())).withRel("owned-projects");

        Link selfLink = linkTo(methodOn(VolunteerController.class)
                .getVolunteer(volunteerToMap.getId())).withSelfRel();

        newDTO.add(allProjectsLink, allOwnedProjectsLink, selfLink);

        return newDTO;
    }
}
