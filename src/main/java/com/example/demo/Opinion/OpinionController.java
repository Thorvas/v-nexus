package com.example.demo.Opinion;

import com.example.demo.Project.ProjectDTO;
import com.example.demo.Volunteer.VolunteerDTO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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

    @Autowired
    private OpinionService opinionService;

    private final String RESOURCE_PATH_LINK = "resource-path";

    private Link rootLink() {
        String ROOT_LINK = "root";
        return linkTo(methodOn(OpinionController.class).getAllOpinions()).withRel(ROOT_LINK);
    }

    /**
     * GET endpoint for opinions. Allows authenticated users to retrieve specific opinion
     *
     * @param id Long id value of retrieved opinion
     * @return JSON response containing retrieved opinion
     */
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<OpinionDTO> getOpinion(@PathVariable Long id) {

        OpinionDTO opinionDTO = opinionService.searchOpinion(id);

        Link selfLink = linkTo(methodOn(OpinionController.class).getOpinion(id)).withRel(RESOURCE_PATH_LINK);

        opinionDTO.add(rootLink(), selfLink);

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

        Link selfLink = linkTo(methodOn(OpinionController.class).getAllOpinions()).withRel(RESOURCE_PATH_LINK);

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

        ProjectDTO projectDTO = opinionService.getDescribedProject(id);

        Link selfLink = linkTo(methodOn(OpinionController.class).getProject(id)).withRel(RESOURCE_PATH_LINK);

        projectDTO.add(rootLink(), selfLink);

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

        VolunteerDTO volunteerDTO = opinionService.getAuthor(id);

        Link selfLink = linkTo(methodOn(OpinionController.class).getAuthor(id)).withRel(RESOURCE_PATH_LINK);

        volunteerDTO.add(rootLink(), selfLink);

        return new ResponseEntity<>(volunteerDTO, HttpStatus.OK);
    }

    /**
     * POST endpoint for opinions. Allows users to create opinions about projects
     *
     * @param projectId Long id value of project that is described
     * @param opinion   Object that defines contents of opinion
     * @return JSON response containing created opinion
     */
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<OpinionDTO> postOpinion(@RequestParam(name = "projectId") Long projectId, @Valid @RequestBody OpinionDTO opinion) {

        OpinionDTO opinionDTO = opinionService.createOpinion(projectId, opinion);

        Link selfLink = linkTo(methodOn(OpinionController.class).postOpinion(projectId, opinion)).withRel(RESOURCE_PATH_LINK);

        opinionDTO.add(rootLink(), selfLink);

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

        OpinionDTO opinionDTO = opinionService.deleteOpinion(id);

        Link selfLink = linkTo(methodOn(OpinionController.class).deleteOpinion(id)).withRel(RESOURCE_PATH_LINK);

        opinionDTO.add(rootLink(), selfLink);

        return new ResponseEntity<>(opinionDTO, HttpStatus.OK);
    }


    /**
     * PATCH endpoint for opinions. Allows users to update their opinions
     *
     * @param id      Long id value of updated opinions
     * @param opinion Updated opinion
     * @return JSON response containing value of updated opinion
     */
    @PatchMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<OpinionDTO> updateOpinion(@PathVariable Long id, @RequestBody OpinionDTO opinion) {

        OpinionDTO opinionDTO = opinionService.updateOpinion(id, opinion);

        Link selfLink = linkTo(methodOn(OpinionController.class).updateOpinion(id, opinion)).withRel(RESOURCE_PATH_LINK);

        opinionDTO.add(rootLink(), selfLink);

        return new ResponseEntity<>(opinionDTO, HttpStatus.OK);
    }
}
