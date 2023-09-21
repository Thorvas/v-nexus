package com.example.demo.Project;

import com.example.demo.Category.Category;
import com.example.demo.Category.CategoryDTO;
import com.example.demo.Error.*;
import com.example.demo.Opinion.Opinion;
import com.example.demo.Opinion.OpinionDTO;
import com.example.demo.Volunteer.Volunteer;
import com.example.demo.Volunteer.VolunteerDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service responsible for project operations
 *
 * @author Thorvas
 */
@Service
public class ProjectService {

    @Autowired
    private ProjectServiceFacade projectServiceFacade;

    @Autowired
    private ProjectRepository projectRepository;

    /**
     * Searches for project based on id parameter
     *
     * @param id Id of searched project
     * @return Optional containing result of search
     */
    public Project findProject(Long id) {

        if (projectRepository.findById(id).isPresent()) {

            return projectRepository.findById(id).get();
        }

        throw new ProjectNotFoundException("Requested project could not be found.");
    }

    /**
     * Sets project's status to FINISHED
     *
     * @param id Id value of edited project
     * @return ProjectDTO of edited project
     */
    public ProjectDTO finishProject(Long id) {

        Project project = findProject(id);

        if (!this.isProjectFinished(project)) {
            if (this.isVolunteerProjectOwner(projectServiceFacade.getLoggedVolunteer(), project) || projectServiceFacade.checkIfAdmin(projectServiceFacade.getLoggedVolunteer())) {

                project.setProjectStatus(ProjectStatus.STATUS_PROGRESS);
                projectRepository.save(project);

                return projectServiceFacade.mapProjectToDTO(project);
            }
            throw new InsufficientPermissionsException("You are not an owner of project and you can't finish it");

        }

        throw new WrongStatusException("You can't close project that is not open");
    }

    /**
     * Sets project's status to CLOSED
     *
     * @param id Id value of edited project
     * @return ProjectDTO of edited project
     */
    public ProjectDTO progressProject(Long id) {

        Project project = findProject(id);

        if (this.isProjectClosed(project)) {
            if (!this.isProjectFinished(project)) {
                if (this.isVolunteerProjectOwner(projectServiceFacade.getLoggedVolunteer(), project) || projectServiceFacade.checkIfAdmin(projectServiceFacade.getLoggedVolunteer())) {
                    project.setProjectStatus(ProjectStatus.STATUS_PROGRESS);
                    projectRepository.save(project);

                    return projectServiceFacade.mapProjectToDTO(project);
                }
                throw new InsufficientPermissionsException("You are not an owner of project and you can't close it");

            }

            throw new WrongStatusException("Project is already finished");
        }

        throw new WrongStatusException("You can't close project that is not open");
    }

    /**
     * Checks whether project capacity is full
     *
     * @param project Inspected project
     * @return Boolean result of check
     */
    public boolean isProjectFull(Project project) {

        int participants = project.getProjectVolunteers().size();

        return participants >= project.getVolunteerCapacity();
    }


    /**
     * Sets project's status to OPEN
     *
     * @param id Id value of edited project
     * @return ProjectDTO of edited project
     */
    public ProjectDTO openProject(Long id) {

        Project project = findProject(id);

        if (this.isProjectOpen(project)) {
            if (!this.isProjectFinished(project)) {
                if (this.isVolunteerProjectOwner(projectServiceFacade.getLoggedVolunteer(), project) || projectServiceFacade.checkIfAdmin(projectServiceFacade.getLoggedVolunteer())) {
                    project.setProjectStatus(ProjectStatus.STATUS_OPEN);
                    projectRepository.save(project);

                    return projectServiceFacade.mapProjectToDTO(project);
                }

                throw new InsufficientPermissionsException("You are not an owner of project and you can't open it");
            }

            throw new WrongStatusException("Project is already finished");
        }

        throw new WrongStatusException("You can't open project that is not closed");
    }

    /**
     * Checks whether project has open status
     *
     * @param project Inspected project
     * @return Boolean value containing result of comparison
     */
    public boolean isProjectOpen(Project project) {

        return project.getProjectStatus().equals(ProjectStatus.STATUS_OPEN);
    }

    /**
     * Checks whether project has closed status
     *
     * @param project Inspected project
     * @return Boolean value containing result of comparison
     */
    public boolean isProjectClosed(Project project) {

        return project.getProjectStatus().equals(ProjectStatus.STATUS_PROGRESS);
    }

    /**
     * Checks whether project has finished status
     *
     * @param project Inspected project
     * @return Boolean value containing result of comparison
     */
    public boolean isProjectFinished(Project project) {

        return project.getProjectStatus().equals(ProjectStatus.STATUS_FINISHED);
    }

    /**
     * Adds volunteer to project
     *
     * @param volunteerId Id value of volunteer that is added to project
     * @param projectId   Id value of project that is modified
     * @return Volunteer that is added to project
     */
    public VolunteerDTO addVolunteerToProject(Long volunteerId, Long projectId) {

        Project project = this.findProject(projectId);
        Volunteer volunteer = projectServiceFacade.findVolunteer(volunteerId);

        if (projectServiceFacade.checkIfAdmin(projectServiceFacade.getLoggedVolunteer())) {

            project.addVolunteerToProject(volunteer);
            projectRepository.save(project);

            return projectServiceFacade.mapVolunteerToDTO(volunteer);
        }

        throw new InsufficientPermissionsException("You cannot add volunteer to project because you are not an administrator.");
    }

    /**
     * Adds category to project
     *
     * @param projectId  Id value of project that is modified
     * @param categoryId Id value of category that is added to project
     * @return Category that is added to project
     */
    public CategoryDTO addCategoryToProject(Long projectId, Long categoryId) {

        Project project = this.findProject(projectId);
        Category category = projectServiceFacade.findCategory(categoryId);

        if (this.isVolunteerProjectOwner(projectServiceFacade.getLoggedVolunteer(), project) || projectServiceFacade.checkIfAdmin(projectServiceFacade.getLoggedVolunteer())) {

            project.addCategoryToProject(category);
            projectRepository.save(project);

            return projectServiceFacade.mapCategoryToDTO(category);
        }

        throw new InsufficientPermissionsException("You cannot add category to project because you are not an owner of project.");
    }

    /**
     * Removes category from project
     *
     * @param projectId  Id value of project that is modified
     * @param categoryId Id value of category that is removed
     * @return Category that is removed from project
     */
    public CategoryDTO removeCategoryFromProject(Long projectId, Long categoryId) {

        Project project = this.findProject(projectId);
        Category category = projectServiceFacade.findCategory(categoryId);

        if (this.isVolunteerProjectOwner(projectServiceFacade.getLoggedVolunteer(), project) || projectServiceFacade.checkIfAdmin(projectServiceFacade.getLoggedVolunteer())) {

            project.removeCategoryFromProject(category);
            projectRepository.save(project);

            return projectServiceFacade.mapCategoryToDTO(category);
        }

        throw new InsufficientPermissionsException("You cannot remove category from project because you are not an owner of project.");
    }

    /**
     * Removes volunteer from project
     *
     * @param volunteerId Id value of volunteer that is removed from project
     * @param projectId   Id value of modified project
     * @return Removed volunteer from project
     */
    public VolunteerDTO removeVolunteerFromProject(Long volunteerId, Long projectId) {

        Volunteer volunteer = projectServiceFacade.findVolunteer(volunteerId);
        Project project = this.findProject(projectId);

        if (projectServiceFacade.checkIfAdmin(projectServiceFacade.getLoggedVolunteer()) || this.isVolunteerProjectOwner(projectServiceFacade.getLoggedVolunteer(), project)) {

            project.removeVolunteerFromProject(volunteer);
            projectRepository.save(project);

            return projectServiceFacade.mapVolunteerToDTO(volunteer);
        }

        throw new InsufficientPermissionsException("You cannot remove volunteer from project because you are not an owner or project.");
    }

    /**
     * Changes owner of project
     *
     * @param volunteerId Id value of volunteer that will be new owner
     * @param projectId   Id value of project that will be modified
     * @return Modified project object
     */
    public ProjectDTO changeOwner(Long volunteerId, Long projectId) {

        Project project = this.findProject(projectId);
        Volunteer volunteer = projectServiceFacade.findVolunteer(volunteerId);

        if (this.isVolunteerProjectOwner(projectServiceFacade.getLoggedVolunteer(), project) || projectServiceFacade.checkIfAdmin(projectServiceFacade.getLoggedVolunteer())) {

            project.setOwnerVolunteer(volunteer);

            projectRepository.save(project);

            return projectServiceFacade.mapProjectToDTO(project);
        }

        throw new InsufficientPermissionsException("You cannot change an owner of project because project does not belong to you.");
    }

    /**
     * Creates project
     *
     * @param requestProject ProjectDTO that represents data of project
     * @return Created project
     */
    public ProjectDTO createProject(ProjectDTO requestProject) {

        Project project = new Project();

        projectServiceFacade.mapDTOToProject(requestProject, project);

        project.setProjectStatus(ProjectStatus.STATUS_OPEN);
        project.setOwnerVolunteer(projectServiceFacade.getLoggedVolunteer());
        project.addVolunteerToProject(projectServiceFacade.getLoggedVolunteer());

        projectRepository.save(project);

        return projectServiceFacade.mapProjectToDTO(project);
    }

    /**
     * Retrieves list of opinions associated with project
     *
     * @param projectId Id value of project that is inspected
     * @return List of opinions associated with project
     */
    public List<OpinionDTO> getOpinions(Long projectId) {

        Project project = this.findProject(projectId);

        List<Opinion> opinions = project.getProjectOpinions();

        if (!opinions.isEmpty()) {

            return opinions.stream()
                    .map(projectServiceFacade::mapOpinionToDTO)
                    .collect(Collectors.toList());
        }

        throw new CollectionEmptyException("Project does not have any opinions yet.");
    }

    /**
     * Retrieves volunteer that is an owner of project
     *
     * @param projectId Id value of inspected project
     * @return Volunteer owner of project
     */
    public VolunteerDTO getOwner(Long projectId) {

        Project project = this.findProject(projectId);

        return projectServiceFacade.mapVolunteerToDTO(project.getOwnerVolunteer());
    }

    /**
     * Searches for projects with date
     *
     * @param date Specified date value
     * @return List of projects matching with concrete date
     */
    public List<ProjectDTO> searchProjectsWithDate(LocalDate date) {

        List<Project> projects = projectRepository.findWithDate(date);

        return projects.stream()
                .map(projectServiceFacade::mapProjectToDTO)
                .collect(Collectors.toList());
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
     * @param projectId  Id value of updated project
     * @param projectDTO ProjectDTO object representing new values that will replace old object
     * @return Updated project
     */
    public ProjectDTO updateProject(Long projectId, ProjectDTO projectDTO) {

        Project sourceProject = this.findProject(projectId);

        if (this.isVolunteerProjectOwner(projectServiceFacade.getLoggedVolunteer(), sourceProject) || projectServiceFacade.checkIfAdmin(projectServiceFacade.getLoggedVolunteer())) {

            projectServiceFacade.mapDTOToProject(projectDTO, sourceProject);

            projectRepository.save(sourceProject);

            return projectServiceFacade.mapProjectToDTO(sourceProject);
        }

        throw new InsufficientPermissionsException("You cannot update that projects because you are not its owner.");

    }

    /**
     * Searches for all projects
     *
     * @return List of projects that were found
     */
    public List<ProjectDTO> searchAllProjects() {

        List<Project> projects = projectRepository.findAll();

        if (!projects.isEmpty()) {

            return projects.stream()
                    .map(projectServiceFacade::mapProjectToDTO)
                    .collect(Collectors.toList());
        }
        throw new CollectionEmptyException("Currently there are no projects in database.");
    }

    /**
     * Searches for project in database using utility method
     *
     * @param id Id value of searched project
     * @return Found project
     */
    public ProjectDTO searchProject(Long id) {

        Project project = this.findProject(id);

        return projectServiceFacade.mapProjectToDTO(project);
    }

    /**
     * Retrieves volunteers associated with project
     *
     * @param projectId Id value of inspected project
     * @return List of volunteers associated with project
     */
    public List<VolunteerDTO> getVolunteers(Long projectId) {

        Project project = this.findProject(projectId);

        if (!project.getProjectVolunteers().isEmpty()) {

            return project.getProjectVolunteers().stream()
                    .map(projectServiceFacade::mapVolunteerToDTO)
                    .collect(Collectors.toList());
        }

        throw new EntityNotPresentInCollectionException("Project does not have any volunteers yet.");
    }

    /**
     * Searches for projects matching given location
     *
     * @param location Location of project
     * @return List of projects that match with location
     */
    public List<ProjectDTO> searchProjectsWithLocation(String location) {

        List<Project> foundProjects = projectRepository.findWithLocation(location);

        if (!foundProjects.isEmpty()) {

            return foundProjects.stream()
                    .map(projectServiceFacade::mapProjectToDTO)
                    .collect(Collectors.toList());
        }

        throw new CollectionEmptyException("Projects with requested location could not be found.");
    }

    /**
     * Searches for projects matching given status
     *
     * @param status Status of project representing whether project is active or not
     * @return List of projects that match with status
     */
    public List<ProjectDTO> searchProjectsWithStatus(ProjectStatus status) {

        List<Project> foundProjects = projectRepository.findWithStatus(status);

        if (!foundProjects.isEmpty()) {

            return foundProjects.stream()
                    .map(projectServiceFacade::mapProjectToDTO)
                    .collect(Collectors.toList());
        }

        throw new CollectionEmptyException("Projects with requested status could not be found.");
    }

    /**
     * Searches for categories associated with project
     *
     * @param projectId Id value of inspected project
     * @return List of categories associated with project
     */
    public List<CategoryDTO> searchCategories(Long projectId) {

        Project project = this.findProject(projectId);

        List<Category> categories = project.getCategories();

        if (!categories.isEmpty()) {

            return categories.stream()
                    .map(projectServiceFacade::mapCategoryToDTO)
                    .toList();
        }

        throw new CollectionEmptyException("Project does not have any categories yet.");
    }

    /**
     * Deletes project
     *
     * @param projectId Id value of project
     * @return Deleted project
     */
    public ProjectDTO deleteProject(Long projectId) {

        Project project = this.findProject(projectId);

        if (this.isVolunteerProjectOwner(projectServiceFacade.getLoggedVolunteer(), project) || projectServiceFacade.checkIfAdmin(projectServiceFacade.getLoggedVolunteer())) {

            projectRepository.delete(project);

            return projectServiceFacade.mapProjectToDTO(project);
        }

        throw new InsufficientPermissionsException("You cannot delete project because you are not its owner");
    }
}
