package com.example.demo.Mapper;

import com.example.demo.Controller.ProjectController;
import com.example.demo.Controller.VolunteerController;
import com.example.demo.DTO.ProjectDTO;
import com.example.demo.DummyObject.Project;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;


@Component
public class ProjectMapper {

    @Autowired
    private ModelMapper modelMapper;


    //TODO: Added links are not correct. All categories and all opinions should point to PROJECT'S resources, not general.
    public ProjectDTO mapProjectToDTO(Project projectToMap) {

        ProjectDTO newDTO = modelMapper.map(projectToMap, ProjectDTO.class);

        Link allParticipantsLink = linkTo(methodOn(ProjectController.class)
                .getVolunteers(projectToMap.getId())).withRel("participating-volunteers");

        Link allCategoriesLink = linkTo(methodOn(ProjectController.class)
                .getCategories(projectToMap.getId())).withRel("categories");

        Link allOpinionsLink = linkTo(methodOn(ProjectController.class)
                .getOpinions(projectToMap.getId())).withRel("opinions");

        Link projectOwnerLink = linkTo(methodOn(VolunteerController.class)
                .getVolunteer(projectToMap.getOwnerVolunteer().getId())).withRel("project-owner");

        Link selfLink = linkTo(methodOn(ProjectController.class)
                .getVolunteers(projectToMap.getId())).withSelfRel();

        Link rootLink = linkTo(methodOn(ProjectController.class)
                .listProjects()).withRel("root");

        newDTO.add(allParticipantsLink, allCategoriesLink, allOpinionsLink, projectOwnerLink, selfLink, rootLink);

        return newDTO;
    }

    public static void mapPropertiesToProject(Project sourceProject, Project targetProject) {

        targetProject.setProjectName(sourceProject.getProjectName());
        targetProject.setProjectDate(sourceProject.getProjectDate());
        targetProject.setProjectVolunteers(sourceProject.getProjectVolunteers());
        targetProject.setProjectDesription(sourceProject.getProjectDesription());
        targetProject.setProjectLocation(sourceProject.getProjectLocation());
        targetProject.setProjectOpinions(sourceProject.getProjectOpinions());
        targetProject.setProjectStatus(sourceProject.isProjectStatus());
        targetProject.setOwnerVolunteer(sourceProject.getOwnerVolunteer());
        targetProject.setCategories(sourceProject.getCategories());

    }
}
