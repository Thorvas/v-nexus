package com.example.demo.Services;

import com.example.demo.DTO.VolunteerDTO;
import com.example.demo.Objects.Project;
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

    public Project saveProject(Project project) {

        return projectRepository.save(project);
    }

    public List<Project> searchProjectsWithDate(LocalDate date) {

        return projectRepository.findWithDate(date);
    }

    public List<Project> matchProjectsWithSkills(VolunteerDTO volunteerDTO) {
        List<Project> foundProjects = projectRepository.findAll();

        List<Project> returnedProjects = foundProjects.stream()
                .filter(project -> project.getRequiredSkills().stream()
                        .anyMatch(skill -> volunteerDTO.getSkills().contains(skill))).distinct().collect(Collectors.toList());

        return returnedProjects;
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
