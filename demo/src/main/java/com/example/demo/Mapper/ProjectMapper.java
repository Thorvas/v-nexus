package com.example.demo.Mapper;

import com.example.demo.DTO.ProjectDTO;
import com.example.demo.DummyObject.Project;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class ProjectMapper {

    @Autowired
    private ModelMapper modelMapper;

    public ProjectDTO mapProjectToDTO(Project projectToMap) {

        ProjectDTO newDTO = modelMapper.map(projectToMap, ProjectDTO.class);

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

    }
}
