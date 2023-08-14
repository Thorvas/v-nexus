package com.example.demo.Opinion;

import com.example.demo.Authentication.AuthenticationService;
import com.example.demo.Project.*;
import com.example.demo.Volunteer.Volunteer;
import com.example.demo.Volunteer.VolunteerDTO;
import com.example.demo.Volunteer.VolunteerMapper;
import com.example.demo.Volunteer.VolunteerService;
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

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * Controller for opinions
 *
 * @author Thorvas
 */
@RestController
@RequestMapping("/api/v1/opinions")
public class OpinionController {

    private final String PROJECT_NOT_FOUND_MESSAGE = "Requested project could not be found.";
    private final String OPINION_NOT_FOUND_MESSAGE = "Requested opinion could not be found.";
    private final String PERMISSION_DENIED_MESSAGE = "You are not permitted to perform this operation.";
    private final String VOLUNTEER_NOT_FOUND_MESSAGE = "Requested volunteer could not be found.";

    @Autowired
    private OpinionService opinionService;

    @Autowired
    private VolunteerMapper volunteerMapper;

    @Autowired
    private OpinionMapper opinionMapper;

    @Autowired
    private ProjectMapper projectMapper;

    @Autowired
    private VolunteerService volunteerService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private AuthenticationService authenticationService;

    private Link rootLink() {
        String ROOT_LINK = "root";
        return linkTo(methodOn(ProjectController.class)
                .listProjects()).withRel(ROOT_LINK);
    }

    /**
     * GET endpoint for opinions. Allows authenticated users to retrieve specific opinion
     *
     * @param id Long id value of retrieved opinion
     * @return JSON response containing retrieved opinion
     */
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<OpinionDTO> getOpinion(@PathVariable Long id) {

        OpinionDTO opinionDTO = opinionService.searchOpinion(id).orElseThrow(() -> new EntityNotFoundException(OPINION_NOT_FOUND_MESSAGE));

        opinionDTO.add(rootLink());

        return new ResponseEntity<>(opinionDTO, HttpStatus.OK);
    }

    /**
     * GET endpoint for opinions. Allows authenticated users to retrieve all opinions
     *
     * @return JSON response containing retrieved opinions
     */
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CollectionModel<OpinionDTO>> getAllOpinions() {

        List<OpinionDTO> opinionDTOs = opinionService.searchAllOpinions();

        Link selfLink = linkTo(methodOn(OpinionController.class)
                .getAllOpinions()).withSelfRel();

        CollectionModel<OpinionDTO> resource = CollectionModel.of(opinionDTOs, selfLink);

        return new ResponseEntity<>(resource, HttpStatus.OK);
    }

    /**
     * GET endpoint for project described by certain opinion
     *
     * @param id Long id value of opinion
     * @return JSON response containing described project
     */
    @GetMapping(value = "/{id}/project", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ProjectDTO> getProject(@PathVariable Long id) {

        Opinion foundOpinion = opinionService.findOpinion(id).orElseThrow(() -> new EntityNotFoundException(OPINION_NOT_FOUND_MESSAGE));

        ProjectDTO projectDTO = opinionService.getDescribedProject(foundOpinion);

        projectDTO.add(rootLink());

        return new ResponseEntity<>(projectDTO, HttpStatus.OK);
    }

    /**
     * GET endpoint for volunteer that is author of certain opinion
     *
     * @param id Long id value of opinion
     * @return JSON response containing author as volunteer
     */
    @GetMapping(value = "/{id}/author", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<VolunteerDTO> getAuthor(@PathVariable Long id) {

        Opinion foundOpinion = opinionService.findOpinion(id).orElseThrow(() -> new EntityNotFoundException(OPINION_NOT_FOUND_MESSAGE));

        VolunteerDTO volunteerDTO = opinionService.getAuthor(foundOpinion);

        volunteerDTO.add(rootLink());

        return new ResponseEntity<>(volunteerDTO, HttpStatus.OK);
    }

    /**
     * POST endpoint for opinions. Allows users to create opinions about projects
     *
     * @param authorId  Long id value of volunteer that is posting opinion
     * @param projectId Long id value of project that is described
     * @param opinion   Object that defines contents of opinion
     * @return JSON response containing created opinion
     */
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<OpinionDTO> postOpinion(@RequestParam(name = "authorId") Long authorId,
                                                  @RequestParam(name = "projectId") Long projectId,
                                                  @Valid @RequestBody OpinionDTO opinion) {

        Volunteer author = volunteerService.findVolunteer(authorId).orElseThrow(() -> new EntityNotFoundException(VOLUNTEER_NOT_FOUND_MESSAGE));
        Project describedProject = projectService.findProject(projectId);

        OpinionDTO opinionDTO = opinionService.createOpinion(author, describedProject, opinion).orElseThrow(() -> new AccessDeniedException(PERMISSION_DENIED_MESSAGE));

        return new ResponseEntity<>(opinionDTO, HttpStatus.CREATED);
    }

    /**
     * DELETE endpoint for opinions. Allows users to delete opinions if they are their authors or administrators
     *
     * @param id Long id value of opinion that is being deleted
     * @return JSON response containing deleted opinion
     */
    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<OpinionDTO> deleteOpinion(@PathVariable Long id) {

        Opinion opinion = opinionService.findOpinion(id).orElseThrow(() -> new EntityNotFoundException(OPINION_NOT_FOUND_MESSAGE));

        OpinionDTO opinionDTO = opinionService.deleteOpinion(opinion).orElseThrow(() -> new AccessDeniedException(PERMISSION_DENIED_MESSAGE));

        return new ResponseEntity<>(opinionDTO, HttpStatus.OK);
    }
}
