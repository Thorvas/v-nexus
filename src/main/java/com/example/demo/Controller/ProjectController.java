package com.example.demo.Controller;

import com.example.demo.DTO.CategoryDTO;
import com.example.demo.DTO.OpinionDTO;
import com.example.demo.DTO.ProjectDTO;
import com.example.demo.DTO.VolunteerDTO;
import com.example.demo.Mapper.CategoryMapper;
import com.example.demo.Mapper.OpinionMapper;
import com.example.demo.Mapper.ProjectMapper;
import com.example.demo.Mapper.VolunteerMapper;
import com.example.demo.Objects.Category;
import com.example.demo.Objects.Opinion;
import com.example.demo.Objects.Project;
import com.example.demo.Objects.Volunteer;
import com.example.demo.Services.AuthenticationService;
import com.example.demo.Services.CategoryService;
import com.example.demo.Services.ProjectService;
import com.example.demo.Services.VolunteerService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * Controller for projects
 *
 * @author Thorvas
 */
@RestController
@RequestMapping("/api/v1/projects")
public class ProjectController {

    private final String PROJECT_NOT_FOUND_MESSAGE = "Requested project could not be found.";

    private final String PERMISSION_DENIED_MESSAGE = "You are not permitted to perform this operation.";
    private final String VOLUNTEER_NOT_FOUND_MESSAGE = "Requested volunteer could not be found.";
    private final String CATEGORY_NOT_FOUND_MESSAGE = "Requested category could not be found.";
    private final String ROOT_LINK = "root";

    @Autowired
    private CategoryService categoryService;
    @Autowired
    private AuthenticationService authenticationService;
    @Autowired
    private ProjectService projectService;
    @Autowired
    private VolunteerService volunteerService;
    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private ProjectMapper projectMapper;
    @Autowired
    private VolunteerMapper volunteerMapper;
    @Autowired
    private OpinionMapper opinionMapper;

    /**
     * POST endpoint for projects. Allows users to create their own projects
     *
     * @param project Project that is published
     * @return JSON response containing published project
     */
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ProjectDTO> saveProject(@Valid @RequestBody ProjectDTO project) {

        Volunteer loggedUser = volunteerService.findVolunteer(volunteerService.getLoggedVolunteer().getId()).orElseThrow(() -> new EntityNotFoundException(VOLUNTEER_NOT_FOUND_MESSAGE));
        Project savedProject = projectService.createProject(loggedUser, project);
        ProjectDTO projectDTO = projectMapper.mapProjectToDTO(savedProject);

        return new ResponseEntity<>(projectDTO, HttpStatus.CREATED);
    }


    /**
     * GET endpoint for projects. Retrieves list of existing projects
     *
     * @return JSON response containing list of existing projects
     */
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CollectionModel<ProjectDTO>> listProjects() {

        List<Project> foundProjects = projectService.searchAllProjects();

        List<ProjectDTO> projectDTOs = foundProjects.stream()
                .map(projectMapper::mapProjectToDTO)
                .collect(Collectors.toList());
        Link selfLink = linkTo(methodOn(ProjectController.class)
                .listProjects()).withSelfRel();

        CollectionModel<ProjectDTO> resource = CollectionModel.of(projectDTOs, selfLink);

        return new ResponseEntity<>(resource, HttpStatus.OK);
    }

    /**
     * GET endpoint for projects. Retrieves project based on id parameter
     *
     * @param id Long id value of retrieved project
     * @return JSON response containing retrieved project
     */
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ProjectDTO> getProject(@PathVariable Long id) {

        Project foundProject = projectService.findProject(id).orElseThrow(() -> new EntityNotFoundException(PROJECT_NOT_FOUND_MESSAGE));

        Link rootLink = linkTo(methodOn(ProjectController.class)
                .listProjects()).withRel(ROOT_LINK);

        ProjectDTO projectDTO = projectMapper.mapProjectToDTO(foundProject);

        projectDTO.add(rootLink);

        return new ResponseEntity<>(projectDTO, HttpStatus.OK);

    }

    /**
     * GET endpoint for volunteers that participate in certain project
     *
     * @param id Long id value of inspected project
     * @return JSON response containing list of volunteers that participate in project
     */
    @GetMapping(value = "/{id}/volunteers", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CollectionModel<VolunteerDTO>> getVolunteers(@PathVariable Long id) {

        Project foundProject = projectService.findProject(id).orElseThrow(() -> new EntityNotFoundException(PROJECT_NOT_FOUND_MESSAGE));

        List<VolunteerDTO> volunteerDTOs = foundProject.getProjectVolunteers().stream()
                .map(volunteerMapper::mapVolunteerToDTO)
                .collect(Collectors.toList());

        Link selfLink = linkTo(methodOn(ProjectController.class)
                .getVolunteers(id)).withSelfRel();
        Link rootLink = linkTo(methodOn(ProjectController.class)
                .listProjects()).withRel(ROOT_LINK);

        CollectionModel<VolunteerDTO> resource = CollectionModel.of(volunteerDTOs, selfLink, rootLink);

        return new ResponseEntity<>(resource, HttpStatus.OK);
    }

    /**
     * GET endpoint for volunteer that is the owner of project
     *
     * @param id Long id value of inspected project
     * @return JSON response containing volunteer being owner of inspected project
     */
    @GetMapping(value = "/{id}/owner", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<VolunteerDTO> getOwner(@PathVariable Long id) {

        Project foundProject = projectService.findProject(id).orElseThrow(() -> new EntityNotFoundException(PROJECT_NOT_FOUND_MESSAGE));

        Link rootLink = linkTo(methodOn(ProjectController.class)
                .listProjects()).withRel(ROOT_LINK);

        VolunteerDTO volunteerDTO = volunteerMapper.mapVolunteerToDTO(foundProject.getOwnerVolunteer());

        volunteerDTO.add(rootLink);

        return new ResponseEntity<>(volunteerDTO, HttpStatus.OK);
    }

    /**
     * GET endpoint for opinions that regard certain project
     *
     * @param id Long id value of inspected project
     * @return JSON response containing list of opinions that are regarding certain project
     */
    @GetMapping(value = "/{id}/opinions", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CollectionModel<OpinionDTO>> getOpinions(@PathVariable Long id) {

        Project foundProject = projectService.findProject(id).orElseThrow(() -> new EntityNotFoundException(PROJECT_NOT_FOUND_MESSAGE));

        Set<Opinion> opinions = foundProject.getProjectOpinions();

        List<OpinionDTO> opinionDTOs = opinions.stream()
                .map(opinionMapper::mapOpinionToDTO)
                .collect(Collectors.toList());

        Link selfLink = linkTo(methodOn(ProjectController.class)
                .getOpinions(id)).withSelfRel();
        Link rootLink = linkTo(methodOn(ProjectController.class)
                .listProjects()).withRel(ROOT_LINK);

        CollectionModel<OpinionDTO> resource = CollectionModel.of(opinionDTOs, selfLink, rootLink);

        return new ResponseEntity<>(resource, HttpStatus.OK);
    }

    /**
     * GET endpoint for projects located within certain location
     *
     * @param location String representing location in which project is located
     * @return JSON response containing list of projects that are located within specified location
     */
    @GetMapping(value = "/location/{location}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CollectionModel<ProjectDTO>> getProjectsWithLocation(@PathVariable String location) {

        List<Project> foundProjects = projectService.searchProjectsWithLocation(location);

        List<ProjectDTO> projectDTOs = foundProjects.stream()
                .map(projectMapper::mapProjectToDTO)
                .collect(Collectors.toList());

        Link selfLink = linkTo(methodOn(ProjectController.class)
                .getProjectsWithLocation(location)).withSelfRel();
        Link rootLink = linkTo(methodOn(ProjectController.class)
                .listProjects()).withRel(ROOT_LINK);

        CollectionModel<ProjectDTO> resource = CollectionModel.of(projectDTOs, selfLink, rootLink);

        return new ResponseEntity<>(resource, HttpStatus.OK);
    }

    /**
     * GET endpoint for projects with certain status - either active of inactive
     *
     * @param status Boolean representing current state of projects. It accepts "true" or "false" to indicate that project is active or inactive
     * @return JSON response containing list of projects that are active or inactive
     */
    @GetMapping(value = "/status/{status}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CollectionModel<ProjectDTO>> getProjectsWithStatus(@PathVariable Boolean status) {

        List<ProjectDTO> projectDTOs = projectService.searchProjectsWithStatus(status)
                .stream()
                .map(projectMapper::mapProjectToDTO)
                .collect(Collectors.toList());

        Link selfLink = linkTo(methodOn(ProjectController.class)
                .getProjectsWithStatus(status)).withSelfRel();
        Link rootLink = linkTo(methodOn(ProjectController.class)
                .listProjects()).withRel(ROOT_LINK);

        CollectionModel<ProjectDTO> resource = CollectionModel.of(projectDTOs, selfLink, rootLink);

        return new ResponseEntity<>(resource, HttpStatus.OK);
    }

    /**
     * GET endpoint for categories associated with certain project
     *
     * @param id Long id value of inspected project
     * @return JSON response containing list of categories associated with project
     */
    @GetMapping(value = "/{id}/categories", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CollectionModel<CategoryDTO>> getCategories(@PathVariable Long id) {

        Project foundProject = projectService.findProject(id).orElseThrow(() -> new EntityNotFoundException(PROJECT_NOT_FOUND_MESSAGE));
        List<CategoryDTO> categories = foundProject.getCategories().stream()
                .map(categoryMapper::mapCategoryToDTO)
                .collect(Collectors.toList());

        Link selfLink = linkTo(methodOn(ProjectController.class)
                .getCategories(id)).withSelfRel();
        Link rootLink = linkTo(methodOn(ProjectController.class)
                .listProjects()).withRel(ROOT_LINK);

        CollectionModel<CategoryDTO> resource = CollectionModel.of(categories, selfLink, rootLink);

        return new ResponseEntity<>(resource, HttpStatus.OK);

    }

    /**
     * GET endpoint for projects that are matching with specified date
     *
     * @param date Date value representing date of project
     * @return JSON response containing list of projects that match with specified date
     */
    @GetMapping(value = "/date/{date}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CollectionModel<ProjectDTO>> getProjectsWithDate(@PathVariable LocalDate date) {

        List<Project> foundProjects = projectService.searchProjectsWithDate(date);

        List<ProjectDTO> projectDTOs = foundProjects.stream()
                .map(projectMapper::mapProjectToDTO)
                .collect(Collectors.toList());

        Link selfLink = linkTo(methodOn(ProjectController.class)
                .getProjectsWithDate(date)).withSelfRel();
        Link rootLink = linkTo(methodOn(ProjectController.class)
                .listProjects()).withRel(ROOT_LINK);

        CollectionModel<ProjectDTO> resource = CollectionModel.of(projectDTOs, selfLink, rootLink);

        return new ResponseEntity<>(resource, HttpStatus.OK);
    }

    /**
     * PATCH endpoint for projects. It updates current project with data provided in request
     *
     * @param project project that will substitute existing project
     * @param id      Long id value of updated project
     * @return JSON response containing updated project
     */
    @PatchMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ProjectDTO> patchProject(@Valid @RequestBody ProjectDTO project,
                                                   @PathVariable Long id) {

        Project foundProject = projectService.findProject(id).orElseThrow(() -> new EntityNotFoundException(PROJECT_NOT_FOUND_MESSAGE));
        Volunteer loggedUser = volunteerService.findVolunteer(volunteerService.getLoggedVolunteer().getId()).orElseThrow(() -> new EntityNotFoundException(VOLUNTEER_NOT_FOUND_MESSAGE));

        if (projectService.isVolunteerProjectOwner(loggedUser, foundProject) || authenticationService.checkIfAdmin(loggedUser)) {

            Project savedProject = projectService.updateProject(foundProject, project);
            ProjectDTO projectDTO = projectMapper.mapProjectToDTO(savedProject);

            return new ResponseEntity<>(projectDTO, HttpStatus.OK);
        } else {

            throw new AccessDeniedException(PERMISSION_DENIED_MESSAGE);
        }
    }

    /**
     * PATCH endpoint for changing owner of project
     *
     * @param projectId   Long id value of updated project
     * @param volunteerId Long id value of new owner
     * @return JSON response containing updated project
     */
    @PatchMapping(value = "/{projectId}/change-owner/{volunteerId}")
    public ResponseEntity<ProjectDTO> changeProjectOwner(@PathVariable Long projectId,
                                                         @PathVariable Long volunteerId
    ) {

        Project foundProject = projectService.findProject(projectId).orElseThrow(() -> new EntityNotFoundException(PROJECT_NOT_FOUND_MESSAGE));
        Volunteer loggedUser = volunteerService.findVolunteer(volunteerService.getLoggedVolunteer().getId()).orElseThrow(() -> new EntityNotFoundException(VOLUNTEER_NOT_FOUND_MESSAGE));
        Volunteer foundVolunteer = volunteerService.findVolunteer(volunteerId).orElseThrow(() -> new EntityNotFoundException(VOLUNTEER_NOT_FOUND_MESSAGE));

        if (projectService.isVolunteerProjectOwner(loggedUser, foundProject) || authenticationService.checkIfAdmin(loggedUser)) {

            projectService.changeOwner(foundVolunteer, foundProject);
            ProjectDTO projectDTO = projectMapper.mapProjectToDTO(foundProject);

            return new ResponseEntity<>(projectDTO, HttpStatus.OK);

        } else {

            throw new AccessDeniedException(PERMISSION_DENIED_MESSAGE);
        }
    }

    /**
     * POST endpoint for adding categories to project
     *
     * @param projectId  Long id value of inspected project
     * @param categoryId Long id value of added category
     * @return JSON response containing updated project
     */
    @PostMapping(value = "/{projectId}/add-category/{categoryId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ProjectDTO> addCategoryToProject(@PathVariable Long projectId,
                                                           @PathVariable Long categoryId
    ) {

        Project foundProject = projectService.findProject(projectId).orElseThrow(() -> new EntityNotFoundException(PROJECT_NOT_FOUND_MESSAGE));
        Volunteer loggedUser = volunteerService.findVolunteer(volunteerService.getLoggedVolunteer().getId()).orElseThrow(() -> new EntityNotFoundException(VOLUNTEER_NOT_FOUND_MESSAGE));
        Category foundCategory = categoryService.findCategory(categoryId).orElseThrow(() -> new EntityNotFoundException(CATEGORY_NOT_FOUND_MESSAGE));

        if (projectService.isVolunteerProjectOwner(loggedUser, foundProject) || authenticationService.checkIfAdmin(loggedUser)) {

            projectService.addCategoryToProject(foundProject, foundCategory);
            ProjectDTO projectDTO = projectMapper.mapProjectToDTO(foundProject);

            return new ResponseEntity<>(projectDTO, HttpStatus.OK);

        } else {

            throw new AccessDeniedException(PERMISSION_DENIED_MESSAGE);
        }
    }

    /**
     * DELETE endpoint for removing categories from project
     *
     * @param projectId  Long id value of inspected project
     * @param categoryId Long id value of removed category
     * @return JSON response containing updated project
     */
    @DeleteMapping(value = "/{projectId}/remove-category/{categoryId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ProjectDTO> removeCategoryFromProject(@PathVariable Long projectId,
                                                                @PathVariable Long categoryId
    ) {

        Project foundProject = projectService.findProject(projectId).orElseThrow(() -> new EntityNotFoundException(PROJECT_NOT_FOUND_MESSAGE));
        Volunteer loggedUser = volunteerService.findVolunteer(volunteerService.getLoggedVolunteer().getId()).orElseThrow(() -> new EntityNotFoundException(VOLUNTEER_NOT_FOUND_MESSAGE));
        Category foundCategory = categoryService.findCategory(categoryId).orElseThrow(() -> new EntityNotFoundException(CATEGORY_NOT_FOUND_MESSAGE));

        if (projectService.isVolunteerProjectOwner(loggedUser, foundProject) || authenticationService.checkIfAdmin(loggedUser)) {

            projectService.removeCategoryFromProject(foundProject, foundCategory);
            ProjectDTO projectDTO = projectMapper.mapProjectToDTO(foundProject);

            return new ResponseEntity<>(projectDTO, HttpStatus.OK);

        } else {

            throw new AccessDeniedException(PERMISSION_DENIED_MESSAGE);
        }
    }

    /**
     * DELETE endpoint for projects. It deletes certain project based on provided id value
     *
     * @param id Long id value of deleted project
     * @return JSON response containing deleted project
     */
    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ProjectDTO> deleteProject(@PathVariable Long id) {

        Project projectToDelete = projectService.findProject(id).orElseThrow(() -> new EntityNotFoundException(PROJECT_NOT_FOUND_MESSAGE));
        Volunteer loggedUser = volunteerService.findVolunteer(volunteerService.getLoggedVolunteer().getId()).orElseThrow(() -> new EntityNotFoundException(VOLUNTEER_NOT_FOUND_MESSAGE));

        if (projectService.isVolunteerProjectOwner(loggedUser, projectToDelete) || authenticationService.checkIfAdmin(loggedUser)) {

            projectService.deleteProject(projectToDelete);

            return new ResponseEntity<>(projectMapper.mapProjectToDTO(projectToDelete), HttpStatus.OK);
        } else {
            throw new AccessDeniedException(PERMISSION_DENIED_MESSAGE);
        }
    }

    /**
     * POST endpoint for adding volunteers to projects. It serves as emergency endpoint for administrators
     *
     * @param projectId   Long id value of project that is edited
     * @param volunteerId Long id value of added volunteer
     * @return JSON response containing updated project
     */
    @PostMapping(value = "/{projectId}/volunteers/{volunteerId}")
    public ResponseEntity<ProjectDTO> addVolunteerToProject(@PathVariable("projectId") Long projectId,
                                                            @PathVariable("volunteerId") Long volunteerId) {

        Project editedProject = projectService.findProject(projectId).orElseThrow(() -> new EntityNotFoundException(PROJECT_NOT_FOUND_MESSAGE));
        Volunteer addedVolunteer = volunteerService.findVolunteer(volunteerId).orElseThrow(() -> new EntityNotFoundException(VOLUNTEER_NOT_FOUND_MESSAGE));
        Volunteer loggedUser = volunteerService.findVolunteer(volunteerService.getLoggedVolunteer().getId()).orElseThrow(() -> new EntityNotFoundException(VOLUNTEER_NOT_FOUND_MESSAGE));

        if (authenticationService.checkIfAdmin(loggedUser)) {

            projectService.addVolunteerToProject(addedVolunteer, editedProject);

            ProjectDTO projectDTO = projectMapper.mapProjectToDTO(editedProject);

            return new ResponseEntity<>(projectDTO, HttpStatus.OK);
        } else {
            throw new AccessDeniedException(PERMISSION_DENIED_MESSAGE);
        }
    }

    /**
     * DELETE endpoint for removing volunteers from projects. It serves as emergency endpoint for administrators
     *
     * @param projectId   Long id value of project that is edited
     * @param volunteerId Long id value of removed volunteer
     * @return JSON response containing edited project
     */
    @DeleteMapping(value = "/{projectId}/volunteers/{volunteerId}")
    public ResponseEntity<ProjectDTO> removeVolunteerFromProject(@PathVariable("projectId") Long projectId,
                                                                 @PathVariable("volunteerId") Long volunteerId) {

        Project editedProject = projectService.findProject(projectId).orElseThrow(() -> new EntityNotFoundException(PROJECT_NOT_FOUND_MESSAGE));
        Volunteer removedVolunteer = volunteerService.findVolunteer(volunteerId).orElseThrow(() -> new EntityNotFoundException(VOLUNTEER_NOT_FOUND_MESSAGE));
        Volunteer loggedUser = volunteerService.findVolunteer(volunteerService.getLoggedVolunteer().getId()).orElseThrow(() -> new EntityNotFoundException(VOLUNTEER_NOT_FOUND_MESSAGE));

        if (authenticationService.checkIfAdmin(loggedUser) || projectService.isVolunteerProjectOwner(loggedUser, editedProject)) {

            projectService.removeVolunteerFromProject(removedVolunteer, editedProject);

            ProjectDTO projectDTO = projectMapper.mapProjectToDTO(editedProject);

            return new ResponseEntity<>(projectDTO, HttpStatus.OK);
        } else {
            throw new AccessDeniedException(PERMISSION_DENIED_MESSAGE);
        }
    }
}
