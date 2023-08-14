package com.example.demo.Project;

import com.example.demo.Authentication.AuthenticationService;
import com.example.demo.Category.Category;
import com.example.demo.Category.CategoryDTO;
import com.example.demo.Category.CategoryMapper;
import com.example.demo.Category.CategoryService;
import com.example.demo.Error.CollectionEmptyException;
import com.example.demo.Error.EntityNotPresentInCollectionException;
import com.example.demo.Error.InsufficientPermissionsException;
import com.example.demo.Error.ProjectNotFoundException;
import com.example.demo.Opinion.Opinion;
import com.example.demo.Opinion.OpinionDTO;
import com.example.demo.Opinion.OpinionMapper;
import com.example.demo.Volunteer.Volunteer;
import com.example.demo.Volunteer.VolunteerDTO;
import com.example.demo.Volunteer.VolunteerMapper;
import com.example.demo.Volunteer.VolunteerService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service responsible for project operations
 */
@Service
public class ProjectService {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private VolunteerMapper volunteerMapper;

    @Autowired
    private VolunteerService volunteerService;

    @Autowired
    private ProjectMapper projectMapper;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private OpinionMapper opinionMapper;

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private AuthenticationService authenticationService;

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
     * Adds volunteer to project
     */
    public VolunteerDTO addVolunteerToProject(Long volunteerId, Long projectId) {

        Project project = this.findProject(projectId);
        Volunteer volunteer = volunteerService.findVolunteer(volunteerId);

        if (authenticationService.checkIfAdmin(volunteerService.getLoggedVolunteer())) {

            project.addVolunteerToProject(volunteer);
            projectRepository.save(project);

            VolunteerDTO volunteerDTO = volunteerMapper.mapVolunteerToDTO(volunteer);

            return volunteerDTO;
        }

        throw new InsufficientPermissionsException("You cannot add volunteer to project because you are not an administrator.");
    }

    /**
     * Adds category to project
     *
     */
    public CategoryDTO addCategoryToProject(Long projectId, Long categoryId) {

        Project project = this.findProject(projectId);
        Category category = categoryService.findCategory(categoryId);

        if (this.isVolunteerProjectOwner(volunteerService.getLoggedVolunteer(), project) || authenticationService.checkIfAdmin(volunteerService.getLoggedVolunteer())) {

            project.addCategoryToProject(category);
            projectRepository.save(project);

            CategoryDTO categoryDTO = categoryMapper.mapCategoryToDTO(category);
            return categoryDTO;
        }

        throw new InsufficientPermissionsException("You cannot add category to project because you are not an owner of project.");
    }

    /**
     * Removes category from project
     *
     */
    public CategoryDTO removeCategoryFromProject(Long projectId, Long categoryId) {

        Project project = this.findProject(projectId);
        Category category = categoryService.findCategory(categoryId);

        if (this.isVolunteerProjectOwner(volunteerService.getLoggedVolunteer(), project) || authenticationService.checkIfAdmin(volunteerService.getLoggedVolunteer())) {

            project.removeCategoryFromProject(category);
            projectRepository.save(project);

            CategoryDTO categoryDTO = categoryMapper.mapCategoryToDTO(category);
            return categoryDTO;
        }

        throw new InsufficientPermissionsException("You cannot remove category from project because you are not an owner of project.");
    }

    /**
     * Removes volunteer from project
     *
     */
    public VolunteerDTO removeVolunteerFromProject(Long volunteerId, Long projectId) {

        Volunteer volunteer = volunteerService.findVolunteer(volunteerId);
        Project project = this.findProject(projectId);

        if (authenticationService.checkIfAdmin(volunteerService.getLoggedVolunteer()) || this.isVolunteerProjectOwner(volunteerService.getLoggedVolunteer(), project)) {

            project.removeVolunteerFromProject(volunteer);
            projectRepository.save(project);

            VolunteerDTO volunteerDTO = volunteerMapper.mapVolunteerToDTO(volunteer);

            return volunteerDTO;
        }

        throw new InsufficientPermissionsException("You cannot remove volunteer from project because you are not an owner or project.");
    }

    /**
     * Changes owner of project
     *
     * @return Modified project object
     */
    public ProjectDTO changeOwner(Long volunteerId, Long projectId) {

        Project project = this.findProject(projectId);
        Volunteer volunteer = volunteerService.findVolunteer(volunteerId);

        if (this.isVolunteerProjectOwner(volunteerService.getLoggedVolunteer(), project) || authenticationService.checkIfAdmin(volunteerService.getLoggedVolunteer())) {

            project.setOwnerVolunteer(volunteer);

            projectRepository.save(project);

            return projectMapper.mapProjectToDTO(project);
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

        Project project = modelMapper.map(requestProject, Project.class);

        project.setOwnerVolunteer(volunteerService.getLoggedVolunteer());
        project.addVolunteerToProject(volunteerService.getLoggedVolunteer());

        projectRepository.save(project);

        ProjectDTO projectDTO = projectMapper.mapProjectToDTO(project);

        return projectDTO;
    }

    public List<OpinionDTO> getOpinions(Long projectId) {

        Project project = this.findProject(projectId);

        List<Opinion> opinions = project.getProjectOpinions();

        if (!opinions.isEmpty()) {
            List<OpinionDTO> opinionDTOs = opinions.stream()
                    .map(opinionMapper::mapOpinionToDTO)
                    .collect(Collectors.toList());

            return opinionDTOs;
        }

        throw new CollectionEmptyException("Project does not have any opinions yet.");
    }

    public VolunteerDTO getOwner(Long projectId) {

        Project project = this.findProject(projectId);

        return volunteerMapper.mapVolunteerToDTO(project.getOwnerVolunteer());
    }

    /**
     * Searches for projects with date
     *
     * @param date Specified date value
     * @return List of projects matching with concrete date
     */
    public List<ProjectDTO> searchProjectsWithDate(LocalDate date) {

        List<Project> projects = projectRepository.findWithDate(date);

        List<ProjectDTO> projectDTOs = projects.stream()
                .map(projectMapper::mapProjectToDTO)
                .collect(Collectors.toList());

        return projectDTOs;
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
     * @param projectDTO    ProjectDTO object representing new values that will replace old object
     * @return Updated project
     */
    public ProjectDTO updateProject(Long projectId, ProjectDTO projectDTO) {

        Project sourceProject = this.findProject(projectId);

        if (this.isVolunteerProjectOwner(volunteerService.getLoggedVolunteer(), sourceProject) || authenticationService.checkIfAdmin(volunteerService.getLoggedVolunteer())) {

            Project project = modelMapper.map(projectDTO, Project.class);
            project.setId(sourceProject.getId());
            projectRepository.save(project);

            return projectMapper.mapProjectToDTO(project);
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

            List<ProjectDTO> projectDTOs = projects.stream()
                    .map(projectMapper::mapProjectToDTO)
                    .collect(Collectors.toList());

            return projectDTOs;
        }
        throw new CollectionEmptyException("Currently there are no projects in database.");
    }

    public ProjectDTO searchProject(Long id) {

        Project project = this.findProject(id);
        ProjectDTO projectDTO = projectMapper.mapProjectToDTO(project);

        return projectDTO;
    }

    public List<VolunteerDTO> getVolunteers(Long projectId) {

        Project project = this.findProject(projectId);

        if (!project.getProjectVolunteers().isEmpty()) {

            List<VolunteerDTO> projectDTOs = project.getProjectVolunteers().stream()
                    .map(volunteerMapper::mapVolunteerToDTO)
                    .collect(Collectors.toList());

            return projectDTOs;
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

            List<ProjectDTO> projectDTOs = foundProjects.stream()
                    .map(projectMapper::mapProjectToDTO)
                    .collect(Collectors.toList());

            return projectDTOs;
        }

        throw new CollectionEmptyException("Projects with requested location could not be found.");
    }

    /**
     * Searches for projects matching given status
     *
     * @param status Status of project representing whether project is active or not
     * @return List of projects that match with status
     */
    public List<ProjectDTO> searchProjectsWithStatus(boolean status) {

        List<Project> foundProjects = projectRepository.findWithStatus(status);

        if (!foundProjects.isEmpty()) {
            List<ProjectDTO> projectDTOs = foundProjects.stream()
                    .map(projectMapper::mapProjectToDTO)
                    .collect(Collectors.toList());

            return projectDTOs;
        }

        throw new CollectionEmptyException("Projects with requested status could not be found.");
    }

    public List<CategoryDTO> searchCategories(Long projectId) {

        Project project = this.findProject(projectId);

        List<Category> categories = project.getCategories();

        if (!categories.isEmpty()) {
            List<CategoryDTO> categoryDTOs = categories.stream()
                    .map(categoryMapper::mapCategoryToDTO)
                    .toList();

            return categoryDTOs;
        }

        throw new CollectionEmptyException("Project does not have any categories yet.");
    }

    /**
     * Deletes project
     *
     */
    public ProjectDTO deleteProject(Long projectId) {

        Project project = this.findProject(projectId);

        if (this.isVolunteerProjectOwner(volunteerService.getLoggedVolunteer(), project) || authenticationService.checkIfAdmin(volunteerService.getLoggedVolunteer())) {

            projectRepository.delete(project);

            ProjectDTO projectDTO = projectMapper.mapProjectToDTO(project);

            return projectDTO;
        }

        throw new InsufficientPermissionsException("You cannot delete project because you are not its owner");
    }
}
