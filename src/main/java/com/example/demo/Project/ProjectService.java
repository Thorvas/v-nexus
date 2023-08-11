package com.example.demo.Project;

import com.example.demo.Category.Category;
import com.example.demo.Volunteer.Volunteer;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Service responsible for project operations
 */
@Service
public class ProjectService {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ModelMapper modelMapper;

    /**
     * Searches for project based on id parameter
     *
     * @param id Id of searched project
     * @return Optional containing result of search
     */
    public Optional<Project> findProject(Long id) {

        return projectRepository.findById(id);
    }

    /**
     * Adds volunteer to project
     *
     * @param addedVolunteer Volunteer that is added to project
     * @param project        Project that is modified
     */
    public void addVolunteerToProject(Volunteer addedVolunteer, Project project) {

        project.addVolunteerToProject(addedVolunteer);
        projectRepository.save(project);
    }

    /**
     * Adds category to project
     *
     * @param project  Project that is modified
     * @param category Category object that is added to project
     */
    public void addCategoryToProject(Project project, Category category) {

        project.addCategoryToProject(category);
        projectRepository.save(project);
    }

    /**
     * Removes category from project
     *
     * @param project  Project that is modified
     * @param category Category object that is removed from project
     */
    public void removeCategoryFromProject(Project project, Category category) {

        project.removeCategoryFromProject(category);
        projectRepository.save(project);
    }

    /**
     * Removes volunteer from project
     *
     * @param removedVolunteer Volunteer object that will be removed
     * @param project          Project that is modified
     */
    public void removeVolunteerFromProject(Volunteer removedVolunteer, Project project) {

        project.removeVolunteerFromProject(removedVolunteer);
        projectRepository.save(project);
    }

    /**
     * Changes owner of project
     *
     * @param user    Volunteer that will be new owner of project
     * @param project Project that is modified
     * @return Modified project object
     */
    public Project changeOwner(Volunteer user, Project project) {

        project.setOwnerVolunteer(user);

        projectRepository.save(project);

        return project;
    }

    /**
     * Creates project
     *
     * @param loggedUser Volunteer that will be an owner of project
     * @param projectDTO ProjectDTO that represents data of project
     * @return Created project
     */
    public Project createProject(Volunteer loggedUser, ProjectDTO projectDTO) {

        Project project = modelMapper.map(projectDTO, Project.class);

        project.setOwnerVolunteer(loggedUser);
        project.addVolunteerToProject(loggedUser);

        projectRepository.save(project);

        return project;
    }

    /**
     * Searches for projects with date
     *
     * @param date Specified date value
     * @return List of projects matching with concrete date
     */
    public List<Project> searchProjectsWithDate(LocalDate date) {

        return projectRepository.findWithDate(date);
    }

    /**
     * Checks whether volunteer is a project owner
     *
     * @param volunteer Inspected volunteer
     * @param project   Checked project
     * @return Boolean containing value of check
     */
    public boolean isVolunteerProjectOwner(Volunteer volunteer, Project project) {

        return volunteer.getId().equals(project.getOwnerVolunteer().getId());
    }

    /**
     * Updates project
     *
     * @param sourceProject Project object representing old values that will be replaced
     * @param projectDTO    ProjectDTO object representing new values that will replace old object
     * @return Updated project
     */
    public Project updateProject(Project sourceProject, ProjectDTO projectDTO) {

        Project project = modelMapper.map(projectDTO, Project.class);

        project.setId(sourceProject.getId());
        projectRepository.save(project);

        return project;
    }

    /**
     * Searches for all projects
     *
     * @return List of projects that were found
     */
    public List<Project> searchAllProjects() {

        return projectRepository.findAll();
    }

    /**
     * Searches for projects matching given location
     *
     * @param location Location of project
     * @return List of projects that match with location
     */
    public List<Project> searchProjectsWithLocation(String location) {

        return projectRepository.findWithLocation(location);
    }

    /**
     * Searches for projects matching given status
     *
     * @param status Status of project representing whether project is active or not
     * @return List of projects that match with status
     */
    public List<Project> searchProjectsWithStatus(boolean status) {

        return projectRepository.findWithStatus(status);
    }

    /**
     * Deletes project
     *
     * @param project Deleted project
     */
    public void deleteProject(Project project) {

        projectRepository.delete(project);
    }
}
