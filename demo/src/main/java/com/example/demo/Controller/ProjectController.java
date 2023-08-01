package com.example.demo.Controller;

import com.example.demo.DTO.CategoryDTO;
import com.example.demo.DTO.OpinionDTO;
import com.example.demo.DTO.ProjectDTO;
import com.example.demo.DTO.VolunteerDTO;
import com.example.demo.Mapper.CategoryMapper;
import com.example.demo.Mapper.OpinionMapper;
import com.example.demo.Mapper.ProjectMapper;
import com.example.demo.Mapper.VolunteerMapper;
import com.example.demo.Objects.CustomUserDetails;
import com.example.demo.Objects.Opinion;
import com.example.demo.Objects.Project;
import com.example.demo.Objects.Volunteer;
import com.example.demo.Services.ProjectService;
import com.example.demo.Services.VolunteerService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/v1/projects")
public class ProjectController {

    private final String PROJECT_NOT_FOUND_MESSAGE = "Requested project could not be found.";
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
    public ResponseEntity<ProjectDTO> saveProject(@RequestBody Project project, @AuthenticationPrincipal CustomUserDetails userDetails) {

        Volunteer loggedUser = volunteerService.findVolunteer(userDetails.getUserData().getReferencedVolunteer().getId()).orElseThrow(() -> new EntityNotFoundException("Entity not found."));
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
                .listProjects()).withRel("root");

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
                .listProjects()).withRel("root");

        CollectionModel<VolunteerDTO> resource = CollectionModel.of(volunteerDTOs, selfLink, rootLink);

        return new ResponseEntity<>(resource, HttpStatus.OK);
    }

    @GetMapping(value = "/{id}/owner", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<VolunteerDTO> getOwner(@PathVariable Long id) {

        Project foundProject = projectService.findProject(id).orElseThrow(() -> new EntityNotFoundException(PROJECT_NOT_FOUND_MESSAGE));

        Link rootLink = linkTo(methodOn(ProjectController.class)
                .listProjects()).withRel("root");

        VolunteerDTO volunteerDTO = volunteerMapper.mapVolunteerToDTO(foundProject.getOwnerVolunteer());

        volunteerDTO.add(rootLink);

        return new ResponseEntity<>(volunteerDTO, HttpStatus.OK);
    }

    @GetMapping(value = "/{id}/opinions", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CollectionModel<OpinionDTO>> getOpinions(@PathVariable Long id) {

        Project foundProject = projectService.findProject(id).orElseThrow(() -> new EntityNotFoundException(PROJECT_NOT_FOUND_MESSAGE));

        List<Opinion> opinions = foundProject.getProjectOpinions();

        List<OpinionDTO> opinionDTOs = opinions.stream()
                .map(opinionMapper::mapOpinionToDTO)
                .collect(Collectors.toList());

        Link selfLink = linkTo(methodOn(ProjectController.class)
                .getOpinions(id)).withSelfRel();
        Link rootLink = linkTo(methodOn(ProjectController.class)
                .listProjects()).withRel("root");

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
                .listProjects()).withRel("root");

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
                .listProjects()).withRel("root");

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
                .listProjects()).withRel("root");

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
                .listProjects()).withRel("root");

        CollectionModel<ProjectDTO> resource = CollectionModel.of(projectDTOs, selfLink, rootLink);

        return new ResponseEntity<>(resource, HttpStatus.OK);
    }

    @PatchMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ProjectDTO> patchProject(@RequestBody Project project,
                                                   @PathVariable Long id,
                                                   @AuthenticationPrincipal CustomUserDetails userDetails) {

        Project foundProject = projectService.findProject(id).orElseThrow(() -> new EntityNotFoundException(PROJECT_NOT_FOUND_MESSAGE));
        Volunteer loggedUser = volunteerService.findVolunteer(userDetails.getUserData().getReferencedVolunteer().getId()).orElseThrow(() -> new EntityNotFoundException("User not found."));

        if (projectService.isVolunteerProjectOwner(loggedUser, foundProject) || loggedUser.getUserData().isAdmin()) {

            Project savedProject = projectService.updateProject(foundProject, project);
            ProjectDTO projectDTO = projectMapper.mapProjectToDTO(savedProject);

            return new ResponseEntity<>(projectDTO, HttpStatus.OK);
        } else {

            throw new BadCredentialsException("You are not permitted to update this project.");
        }
    }

    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ProjectDTO> deleteProject(@PathVariable Long id, @AuthenticationPrincipal CustomUserDetails userDetails) {

        Project projectToDelete = projectService.findProject(id).orElseThrow(() -> new EntityNotFoundException(PROJECT_NOT_FOUND_MESSAGE));
        Volunteer loggedUser = volunteerService.findVolunteer(userDetails.getUserData().getReferencedVolunteer().getId()).orElseThrow(() -> new EntityNotFoundException("User not found."));

        if (projectService.isVolunteerProjectOwner(loggedUser, projectToDelete) || loggedUser.getUserData().isAdmin()) {

            projectService.deleteProject(projectToDelete);

            return new ResponseEntity<>(projectMapper.mapProjectToDTO(projectToDelete), HttpStatus.OK);
        } else {
            throw new BadCredentialsException("You do not have permission to perform that operation.");
        }
    }

    @PatchMapping(value = "/{projectId}/volunteers/{volunteerId}")
    public ResponseEntity<ProjectDTO> addVolunteerToProject(@PathVariable("projectId") Long projectId,
                                                            @PathVariable("volunteerId") Long volunteerId,
                                                            @AuthenticationPrincipal CustomUserDetails userDetails) {

        Project editedProject = projectService.findProject(projectId).orElseThrow(() -> new EntityNotFoundException("Requested project could not be found."));
        Volunteer addedVolunteer = volunteerService.findVolunteer(volunteerId).orElseThrow(() -> new EntityNotFoundException("Requested volunteer could not be found."));
        Volunteer loggedUser = volunteerService.findVolunteer(userDetails.getUserData().getReferencedVolunteer().getId()).orElseThrow(() -> new EntityNotFoundException("Could not find an entity."));

        if (loggedUser.getUserData().isAdmin()) {

            projectService.addVolunteerToProject(addedVolunteer, editedProject);

            ProjectDTO projectDTO = projectMapper.mapProjectToDTO(editedProject);

            return new ResponseEntity<>(projectDTO, HttpStatus.OK);
        } else {
            throw new BadCredentialsException("You are not permitted to perform this operation.");
        }
    }

    @DeleteMapping(value = "/{projectId}/volunteers/{volunteerId}")
    public ResponseEntity<ProjectDTO> removeVolunteerFromProject(@PathVariable("projectId") Long projectId,
                                                                 @PathVariable("volunteerId") Long volunteerId,
                                                                 @AuthenticationPrincipal CustomUserDetails userDetails) {

        Project editedProject = projectService.findProject(projectId).orElseThrow(() -> new EntityNotFoundException("Requested project was not found."));
        Volunteer removedVolunteer = volunteerService.findVolunteer(volunteerId).orElseThrow(() -> new EntityNotFoundException("Requested volunteer was not found."));
        Volunteer loggedUser = volunteerService.findVolunteer(userDetails.getUserData().getReferencedVolunteer().getId()).orElseThrow(() -> new EntityNotFoundException("Could not find an entity."));

        if (loggedUser.getUserData().isAdmin()) {

            projectService.removeVolunteerFromProject(removedVolunteer, editedProject);

            ProjectDTO projectDTO = projectMapper.mapProjectToDTO(editedProject);

            return new ResponseEntity<>(projectDTO, HttpStatus.OK);
        } else {
            throw new IllegalArgumentException("You are not permitted to perform this operation.");
        }
    }
}
