package com.example.demo.Mapper;

import com.example.demo.Controller.OpinionController;
import com.example.demo.Controller.ProjectController;
import com.example.demo.DTO.VolunteerDTO;
import com.example.demo.DummyObject.Volunteer;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;


@Component
public class VolunteerMapper {

    @Autowired
    private ModelMapper modelMapper;

    public VolunteerDTO mapVolunteerToDTO(Volunteer volunteerToMap) {

        VolunteerDTO newDTO = modelMapper.map(volunteerToMap, VolunteerDTO.class);

        newDTO.setParticipatingProjects(volunteerToMap.getParticipatingProjects().stream()
                .map(singleProject -> WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(ProjectController.class)
                        .getProject(singleProject.getId())).withRel("participating-project"))
                .collect(Collectors.toList()));

        newDTO.setOwnedProjects(volunteerToMap.getOwnedProjects().stream()
                .map(singleProject -> WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(ProjectController.class)
                        .getProject(singleProject.getId())).withRel("owned-project"))
                .collect(Collectors.toList()));

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
