package com.example.demo.Controller;

import com.example.demo.DTO.CategoryDTO;
import com.example.demo.DTO.OpinionDTO;
import com.example.demo.DTO.ProjectDTO;
import com.example.demo.DTO.VolunteerDTO;
import com.example.demo.Mapper.CategoryMapper;
import com.example.demo.Mapper.OpinionMapper;
import com.example.demo.Mapper.ProjectMapper;
import com.example.demo.Mapper.VolunteerMapper;
import com.example.demo.Objects.Opinion;
import com.example.demo.Objects.Project;
import com.example.demo.Objects.Volunteer;
import com.example.demo.Services.AuthenticationService;
import com.example.demo.Services.ProjectService;
import com.example.demo.Services.VolunteerService;
import jakarta.persistence.EntityNotFoundException;
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

@RestController
@RequestMapping("/api/v1/projects")
public class ProjectController {

    private final String PROJECT_NOT_FOUND_MESSAGE = "Requested project could not be found.";

    private final String PERMISSION_DENIED_MESSAGE = "You are not permitted to perform this operation.";
    private final String VOLUNTEER_NOT_FOUND_MESSAGE = "Requested volunteer could not be found.";
    private final String ROOT_LINK = "root";

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

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ProjectDTO> saveProject(@RequestBody Project project) {

        Volunteer loggedUser = volunteerService.findVolunteer(volunteerService.getLoggedVolunteer().getId()).orElseThrow(() -> new EntityNotFoundException(VOLUNTEER_NOT_FOUND_MESSAGE));
        Project savedProject = projectService.createProject(loggedUser, project);
        ProjectDTO projectDTO = projectMapper.mapProjectToDTO(savedProject);

        return new ResponseEntity<>(projectDTO, HttpStatus.CREATED);
    }

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

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ProjectDTO> getProject(@PathVariable Long id) {

        Project foundProject = projectService.findProject(id).orElseThrow(() -> new EntityNotFoundException(PROJECT_NOT_FOUND_MESSAGE));

        Link rootLink = linkTo(methodOn(ProjectController.class)
                .listProjects()).withRel(ROOT_LINK);

        ProjectDTO projectDTO = projectMapper.mapProjectToDTO(foundProject);

        projectDTO.add(rootLink);

        return new ResponseEntity<>(projectDTO, HttpStatus.OK);

    }

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

    @GetMapping(value = "/{id}/owner", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<VolunteerDTO> getOwner(@PathVariable Long id) {

        Project foundProject = projectService.findProject(id).orElseThrow(() -> new EntityNotFoundException(PROJECT_NOT_FOUND_MESSAGE));

        Link rootLink = linkTo(methodOn(ProjectController.class)
                .listProjects()).withRel(ROOT_LINK);

        VolunteerDTO volunteerDTO = volunteerMapper.mapVolunteerToDTO(foundProject.getOwnerVolunteer());

        volunteerDTO.add(rootLink);

        return new ResponseEntity<>(volunteerDTO, HttpStatus.OK);
    }

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

    @PatchMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ProjectDTO> patchProject(@RequestBody Project project,
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

    @PatchMapping(value = "/{projectId}/volunteers/{volunteerId}")
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
