package com.example.demo.Services;

import com.example.demo.DTO.ProjectDTO;
import com.example.demo.Objects.Category;
import com.example.demo.Objects.Project;
import com.example.demo.Objects.Volunteer;
import com.example.demo.Repositories.ProjectRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class ProjectService {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ModelMapper modelMapper;

    public Optional<Project> findProject(Long id) {

        return projectRepository.findById(id);
    }

    public void addVolunteerToProject(Volunteer addedVolunteer, Project project) {

        project.addVolunteerToProject(addedVolunteer);
        projectRepository.save(project);
    }

    public void addCategoryToProject(Project project, Category category) {

        project.addCategoryToProject(category);
        projectRepository.save(project);
    }

    public void removeCategoryFromProject(Project project, Category category) {

        project.removeCategoryFromProject(category);
        projectRepository.save(project);
    }

    public void removeVolunteerFromProject(Volunteer removedVolunteer, Project project) {

        project.removeVolunteerFromProject(removedVolunteer);
        projectRepository.save(project);
    }

    public Project saveProject(Project project) {

        return projectRepository.save(project);
    }

    public Project changeOwner(Volunteer user, Project project) {

        project.setOwnerVolunteer(user);

        projectRepository.save(project);

        return project;
    }

    public Project createProject(Volunteer loggedUser, ProjectDTO projectDTO) {

        Project project = modelMapper.map(projectDTO, Project.class);

        project.setOwnerVolunteer(loggedUser);
        project.addVolunteerToProject(loggedUser);

        projectRepository.save(project);

        return project;
    }

    public List<Project> searchProjectsWithDate(LocalDate date) {

        return projectRepository.findWithDate(date);
    }

    public boolean isVolunteerProjectOwner(Volunteer volunteer, Project project) {

        return volunteer.getId().equals(project.getOwnerVolunteer().getId());
    }

    public Project updateProject(Project sourceProject, ProjectDTO projectDTO) {

        Project project = modelMapper.map(projectDTO, Project.class);

        project.setId(sourceProject.getId());
        projectRepository.save(project);

        return project;
    }

    public List<Project> searchAllProjects() {

        return projectRepository.findAll();
    }

    public List<Project> searchProjectsWithLocation(String location) {

        return projectRepository.findWithLocation(location);
    }

    public List<Project> searchProjectsWithStatus(boolean status) {

        return projectRepository.findWithStatus(status);
    }

    public void deleteProject(Project project) {

        projectRepository.delete(project);
    }
}
