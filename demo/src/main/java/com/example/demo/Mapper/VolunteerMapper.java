package com.example.demo.Mapper;

import com.example.demo.Controller.VolunteerController;
import com.example.demo.DTO.VolunteerDTO;
import com.example.demo.Objects.Volunteer;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;


@Component
public class VolunteerMapper {

    @Autowired
    private ModelMapper modelMapper;

    public VolunteerDTO mapVolunteerToDTO(Volunteer volunteerToMap) {

        VolunteerDTO newDTO = modelMapper.map(volunteerToMap, VolunteerDTO.class);

        Link allProjectsLink = linkTo(methodOn(VolunteerController.class)
                .getProjects(volunteerToMap.getId())).withRel("participated-projects");

        Link allOwnedProjectsLink = linkTo(methodOn(VolunteerController.class)
                .getOwnedProjects(volunteerToMap.getId())).withRel("owned-projects");

        Link selfLink = linkTo(methodOn(VolunteerController.class)
                .getVolunteer(volunteerToMap.getId())).withSelfRel();

        Link rootLink = linkTo(methodOn(VolunteerController.class)
                .getVolunteers()).withRel("root");

        newDTO.add(allProjectsLink, allOwnedProjectsLink, selfLink, rootLink);

        return newDTO;
    }

    public static void mapPropertiesToVolunteer(Volunteer sourceVolunteer, Volunteer targetVolunteer) {

        targetVolunteer.setName(sourceVolunteer.getName());
        targetVolunteer.setSurname(sourceVolunteer.getSurname());
        targetVolunteer.setDateOfBirth(sourceVolunteer.getDateOfBirth());
        targetVolunteer.setContact(sourceVolunteer.getContact());
        targetVolunteer.setSkills(sourceVolunteer.getSkills());
        targetVolunteer.setParticipatingProjects(sourceVolunteer.getParticipatingProjects());
        targetVolunteer.setReputation(sourceVolunteer.getReputation());
        targetVolunteer.setInterests(sourceVolunteer.getInterests());

    }
}
