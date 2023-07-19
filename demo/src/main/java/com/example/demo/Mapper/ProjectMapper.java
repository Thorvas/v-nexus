package com.example.demo.Mapper;

import com.example.demo.Controller.CategoryController;
import com.example.demo.Controller.OpinionController;
import com.example.demo.Controller.ProjectController;
import com.example.demo.Controller.VolunteerController;
import com.example.demo.DTO.ProjectDTO;
import com.example.demo.DummyObject.Project;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

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
                .getVolunteers(projectToMap.getId())).withRel("all-participating-volunteers");

        Link allCategoriesLink = linkTo(methodOn(ProjectController.class)
                .getCategories(projectToMap.getId())).withRel("all-categories");

        Link allOpinionsLink = linkTo(methodOn(ProjectController.class)
                .getOpinions(projectToMap.getId())).withRel("all-opinions");

        newDTO.setProjectVolunteers(projectToMap.getProjectVolunteers().stream()
                .map(volunteer -> linkTo(methodOn(VolunteerController.class)
                        .getVolunteer(volunteer.getId())).withRel("participating-volunteer"))
                .collect(Collectors.collectingAndThen(
                        Collectors.toList(),
                        list -> {
                            list.add(allParticipantsLink);
                            return list;
                        }
                )));

        newDTO.setProjectOpinions(projectToMap.getProjectOpinions().stream()
                .map(opinion -> linkTo(methodOn(OpinionController.class)
                        .getOpinion(opinion.getId())).withRel("project-opinion"))
                .collect(Collectors.collectingAndThen(
                        Collectors.toList(),
                        list -> {
                            list.add(allOpinionsLink);
                            return list;
                        }
                )));

        newDTO.setCategories(projectToMap.getCategories().stream()
                .map(category -> linkTo(methodOn(CategoryController.class)
                        .retrieveCategory(category.getId())).withRel("project-category"))
                .collect(Collectors.collectingAndThen(
                        Collectors.toList(),
                        list -> {
                            list.add(allCategoriesLink);
                            return list;
                        })
                ));

        newDTO.setProjectOwner(linkTo(methodOn(VolunteerController.class)
                .getVolunteer(projectToMap.getOwnerVolunteer().getId())).withRel("project-owner"));

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
