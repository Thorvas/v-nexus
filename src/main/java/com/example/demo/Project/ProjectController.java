package com.example.demo.Project;

import com.example.demo.Category.CategoryDTO;
import com.example.demo.Category.CategoryService;
import com.example.demo.Opinion.OpinionDTO;
import com.example.demo.Volunteer.VolunteerDTO;
import com.example.demo.Volunteer.VolunteerService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

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

    @Autowired
    private CategoryService categoryService;
    @Autowired
    private ProjectService projectService;
    @Autowired
    private VolunteerService volunteerService;

    private final String RESOURCE_PATH_LINK = "resource-path";

    private Link rootLink() {
        String ROOT_LINK = "root";
        return linkTo(methodOn(ProjectController.class)
                .listProjects()).withRel(ROOT_LINK);
    }

    /**
     * POST endpoint for projects. Allows users to create their own projects
     *
     * @param project Project that is published
     * @return JSON response containing published project
     */
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ProjectDTO> saveProject(@Valid @RequestBody ProjectDTO project) {

        ProjectDTO projectDTO = projectService.createProject(project);

        Link selfLink = linkTo(methodOn(ProjectController.class)
                .saveProject(project)).withRel(RESOURCE_PATH_LINK);

        projectDTO.add(rootLink(), selfLink);

        return new ResponseEntity<>(projectDTO, HttpStatus.CREATED);
    }


    /**
     * GET endpoint for projects. Retrieves list of existing projects
     *
     * @return JSON response containing list of existing projects
     */
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CollectionModel<ProjectDTO>> listProjects() {

        List<ProjectDTO> projectDTOs = projectService.searchAllProjects();
        Link selfLink = linkTo(methodOn(ProjectController.class)
                .listProjects()).withRel(RESOURCE_PATH_LINK);

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

        ProjectDTO projectDTO = projectService.searchProject(id);

        Link selfLink = linkTo(methodOn(ProjectController.class)
                .getProject(id)).withRel(RESOURCE_PATH_LINK);

        projectDTO.add(rootLink(), selfLink);

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

        List<VolunteerDTO> volunteerDTOs = projectService.getVolunteers(id);

        Link selfLink = linkTo(methodOn(ProjectController.class)
                .getVolunteers(id)).withRel(RESOURCE_PATH_LINK);

        CollectionModel<VolunteerDTO> resource = CollectionModel.of(volunteerDTOs, selfLink, rootLink());

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

        VolunteerDTO volunteerDTO = projectService.getOwner(id);

        Link selfLink = linkTo(methodOn(ProjectController.class)
                .getOwner(id)).withRel(RESOURCE_PATH_LINK);

        volunteerDTO.add(rootLink(), selfLink);

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


        List<OpinionDTO> opinionDTOs = projectService.getOpinions(id);

        Link selfLink = linkTo(methodOn(ProjectController.class)
                .getOpinions(id)).withRel(RESOURCE_PATH_LINK);

        CollectionModel<OpinionDTO> resource = CollectionModel.of(opinionDTOs, selfLink, rootLink());

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

        List<ProjectDTO> projectDTOs = projectService.searchProjectsWithLocation(location);

        Link selfLink = linkTo(methodOn(ProjectController.class)
                .getProjectsWithLocation(location)).withRel(RESOURCE_PATH_LINK);

        CollectionModel<ProjectDTO> resource = CollectionModel.of(projectDTOs, selfLink, rootLink());

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

        List<ProjectDTO> projectDTOs = projectService.searchProjectsWithStatus(status);

        Link selfLink = linkTo(methodOn(ProjectController.class)
                .getProjectsWithStatus(status)).withRel(RESOURCE_PATH_LINK);

        CollectionModel<ProjectDTO> resource = CollectionModel.of(projectDTOs, selfLink, rootLink());

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

        List<CategoryDTO> categories = projectService.searchCategories(id);

        Link selfLink = linkTo(methodOn(ProjectController.class)
                .getCategories(id)).withRel(RESOURCE_PATH_LINK);

        CollectionModel<CategoryDTO> resource = CollectionModel.of(categories, selfLink, rootLink());

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

        List<ProjectDTO> projectDTOs = projectService.searchProjectsWithDate(date);

        Link selfLink = linkTo(methodOn(ProjectController.class)
                .getProjectsWithDate(date)).withRel(RESOURCE_PATH_LINK);

        CollectionModel<ProjectDTO> resource = CollectionModel.of(projectDTOs, selfLink, rootLink());

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

        ProjectDTO projectDTO = projectService.updateProject(id, project);

        Link selfLink = linkTo(methodOn(ProjectController.class)
                .patchProject(project, id)).withRel(RESOURCE_PATH_LINK);

        projectDTO.add(rootLink(), selfLink);

        return new ResponseEntity<>(projectDTO, HttpStatus.OK);
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

        ProjectDTO projectDTO = projectService.changeOwner(volunteerId, projectId);

        Link selfLink = linkTo(methodOn(ProjectController.class)
                .changeProjectOwner(projectId, volunteerId)).withRel(RESOURCE_PATH_LINK);

        projectDTO.add(rootLink(), selfLink);

        return new ResponseEntity<>(projectDTO, HttpStatus.OK);
    }

    /**
     * POST endpoint for adding categories to project
     *
     * @param projectId  Long id value of inspected project
     * @param categoryId Long id value of added category
     * @return JSON response containing updated project
     */
    @PostMapping(value = "/{projectId}/add-category/{categoryId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CategoryDTO> addCategoryToProject(@PathVariable Long projectId,
                                                            @PathVariable Long categoryId
    ) {

        CategoryDTO categoryDTO = projectService.addCategoryToProject(projectId, categoryId);

        Link selfLink = linkTo(methodOn(ProjectController.class)
                .addCategoryToProject(projectId, categoryId)).withRel(RESOURCE_PATH_LINK);

        categoryDTO.add(rootLink(), selfLink);

        return new ResponseEntity<>(categoryDTO, HttpStatus.OK);
    }

    /**
     * DELETE endpoint for removing categories from project
     *
     * @param projectId  Long id value of inspected project
     * @param categoryId Long id value of removed category
     * @return JSON response containing updated project
     */
    @DeleteMapping(value = "/{projectId}/remove-category/{categoryId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CategoryDTO> removeCategoryFromProject(@PathVariable Long projectId,
                                                                 @PathVariable Long categoryId
    ) {


        CategoryDTO categoryDTO = projectService.removeCategoryFromProject(projectId, categoryId);

        Link selfLink = linkTo(methodOn(ProjectController.class)
                .removeCategoryFromProject(projectId, categoryId)).withRel(RESOURCE_PATH_LINK);

        categoryDTO.add(rootLink(), selfLink);

        return new ResponseEntity<>(categoryDTO, HttpStatus.OK);
    }

    /**
     * DELETE endpoint for projects. It deletes certain project based on provided id value
     *
     * @param id Long id value of deleted project
     * @return JSON response containing deleted project
     */
    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ProjectDTO> deleteProject(@PathVariable Long id) {

        ProjectDTO projectDTO = projectService.deleteProject(id);

        Link selfLink = linkTo(methodOn(ProjectController.class)
                .deleteProject(id)).withRel(RESOURCE_PATH_LINK);

        projectDTO.add(rootLink(), selfLink);

        return new ResponseEntity<>(projectDTO, HttpStatus.OK);
    }

    /**
     * POST endpoint for adding volunteers to projects. It serves as emergency endpoint for administrators
     *
     * @param projectId   Long id value of project that is edited
     * @param volunteerId Long id value of added volunteer
     * @return JSON response containing updated project
     */
    @PostMapping(value = "/{projectId}/volunteers/{volunteerId}")
    public ResponseEntity<VolunteerDTO> addVolunteerToProject(@PathVariable("projectId") Long projectId,
                                                              @PathVariable("volunteerId") Long volunteerId) {

        VolunteerDTO volunteerDTO = projectService.addVolunteerToProject(volunteerId, projectId);

        Link selfLink = linkTo(methodOn(ProjectController.class)
                .addVolunteerToProject(projectId, volunteerId)).withRel(RESOURCE_PATH_LINK);

        volunteerDTO.add(rootLink(), selfLink);

        return new ResponseEntity<>(volunteerDTO, HttpStatus.OK);
    }

    /**
     * DELETE endpoint for removing volunteers from projects. It serves as emergency endpoint for administrators
     *
     * @param projectId   Long id value of project that is edited
     * @param volunteerId Long id value of removed volunteer
     * @return JSON response containing edited project
     */
    @DeleteMapping(value = "/{projectId}/volunteers/{volunteerId}")
    public ResponseEntity<VolunteerDTO> removeVolunteerFromProject(@PathVariable("projectId") Long projectId,
                                                                   @PathVariable("volunteerId") Long volunteerId) {

        VolunteerDTO volunteerDTO = projectService.removeVolunteerFromProject(volunteerId, projectId);

        Link selfLink = linkTo(methodOn(ProjectController.class)
                .removeVolunteerFromProject(projectId, volunteerId)).withRel(RESOURCE_PATH_LINK);

        volunteerDTO.add(rootLink(), selfLink);

        return new ResponseEntity<>(volunteerDTO, HttpStatus.OK);
    }
}
