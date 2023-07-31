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
import jakarta.validation.Valid;
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

        Volunteer foundVolunteer = volunteerService.findVolunteer(userDetails.getUserData().getReferencedVolunteer().getId()).orElseThrow(() -> new EntityNotFoundException("Entity not found."));

        project.setOwnerVolunteer(foundVolunteer);
        project.addVolunteerToProject(foundVolunteer);

        Project savedProject = projectService.saveProject(project);

        return new ResponseEntity<>(projectMapper.mapProjectToDTO(savedProject), HttpStatus.CREATED);
    }

    @PatchMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ProjectDTO> updateProject(@PathVariable Long id, @RequestBody @Valid Project project, @AuthenticationPrincipal CustomUserDetails userDetails) {

        Project foundProject = projectService.findProject(id).orElseThrow(() -> new EntityNotFoundException("Entity not found."));
        Volunteer currentUser = volunteerService.findVolunteer(userDetails.getUserData().getReferencedVolunteer().getId()).orElseThrow(() -> new EntityNotFoundException("Entity not found."));

        if (foundProject.getOwnerVolunteer().getId().equals(currentUser.getId())) {

            project.setId(foundProject.getId());
            Project savedProject = projectService.saveProject(project);
            ProjectDTO returnedProject = projectMapper.mapProjectToDTO(savedProject);

            return new ResponseEntity<>(returnedProject, HttpStatus.OK);
        } else {
            throw new BadCredentialsException("Project you are trying to edit does not belong to you.");
        }
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
    public ResponseEntity<ProjectDTO> patchProject(@RequestBody Project project, @PathVariable Long id) {

        if (project != null) {

            Project foundProject = projectService.findProject(id).orElseThrow(() -> new EntityNotFoundException(PROJECT_NOT_FOUND_MESSAGE));

            ProjectDTO projectDTO = projectMapper.mapProjectToDTO(foundProject);

            return new ResponseEntity<>(projectDTO, HttpStatus.OK);
        } else {

            throw new IllegalArgumentException("Updated project cannot be null!");
        }
    }

    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ProjectDTO> deleteProject(@PathVariable Long id) {

        if (id != null) {

            Project projectToDelete = projectService.findProject(id).orElseThrow(() -> new EntityNotFoundException(PROJECT_NOT_FOUND_MESSAGE));

            projectService.deleteProject(projectToDelete);

            return new ResponseEntity<>(projectMapper.mapProjectToDTO(projectToDelete), HttpStatus.OK);
        } else {
            throw new IllegalArgumentException("Id of requested entity should not be null.");
        }
    }

    @PatchMapping(value = "/{projectId}/volunteers/{volunteerId}")
    public ResponseEntity<ProjectDTO> addVolunteerToProject(@PathVariable("projectId") Long projectId,
                                                            @PathVariable("volunteerId") Long volunteerId) {

        Project editedProject = projectService.findProject(projectId).orElseThrow(() -> new EntityNotFoundException("Requested project could not be found."));
        Volunteer addedVolunteer = volunteerService.findVolunteer(volunteerId).orElseThrow(() -> new EntityNotFoundException("Requested volunteer could not be found."));

        editedProject.addVolunteerToProject(addedVolunteer);

        projectService.saveProject(editedProject);

        ProjectDTO projectDTO = projectMapper.mapProjectToDTO(editedProject);

        return new ResponseEntity<>(projectDTO, HttpStatus.OK);
    }

    @DeleteMapping(value = "/{projectId}/volunteers/{volunteerId}")
    public ResponseEntity<ProjectDTO> removeVolunteerFromProject(@PathVariable("projectId") Long projectId,
                                                                 @PathVariable("volunteerId") Long volunteerId) {

        Project editedProject = projectService.findProject(projectId).orElseThrow(() -> new EntityNotFoundException("Requested project was not found."));
        Volunteer removedVolunteer = volunteerService.findVolunteer(volunteerId).orElseThrow(() -> new EntityNotFoundException("Requested volunteer was not found."));

        if (editedProject.getProjectVolunteers().contains(removedVolunteer)) {

            editedProject.removeVolunteerFromProject(removedVolunteer);
            projectService.saveProject(editedProject);

            ProjectDTO projectDTO = projectMapper.mapProjectToDTO(editedProject);

            return new ResponseEntity<>(projectDTO, HttpStatus.OK);
        } else {
            throw new IllegalArgumentException("Requested volunteer does not participate in this project.");
        }
    }
}
