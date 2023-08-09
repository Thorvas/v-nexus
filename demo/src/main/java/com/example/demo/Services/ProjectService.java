package com.example.demo.Services;

import com.example.demo.DTO.VolunteerDTO;
import com.example.demo.Objects.Project;
import com.example.demo.Objects.Volunteer;
import com.example.demo.Repositories.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProjectService {

    @Autowired
    private ProjectRepository projectRepository;


    public Optional<Project> findProject(Long id) {

        return projectRepository.findById(id);
    }

    public void addVolunteerToProject(Volunteer addedVolunteer, Project project) {

        project.addVolunteerToProject(addedVolunteer);
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

    public Project createProject(Volunteer loggedUser, Project project) {

        project.setOwnerVolunteer(loggedUser);
        project.addVolunteerToProject(loggedUser);

        projectRepository.save(project);

        return project;
    }

    public List<Project> searchProjectsWithDate(LocalDate date) {

        return projectRepository.findWithDate(date);
    }

    public List<Project> matchProjectsWithSkills(VolunteerDTO volunteerDTO) {
        List<Project> foundProjects = projectRepository.findAll();

        return foundProjects.stream()
                .filter(project -> project.getRequiredSkills().stream()
                        .anyMatch(skill -> volunteerDTO.getSkills().contains(skill))).distinct().collect(Collectors.toList());
    }

    public boolean isVolunteerProjectOwner(Volunteer volunteer, Project project) {

        return volunteer.getId().equals(project.getOwnerVolunteer().getId());
    }

    public Project updateProject(Project sourceProject, Project targetProject) {

        targetProject.setId(sourceProject.getId());
        projectRepository.save(targetProject);

        return targetProject;
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
