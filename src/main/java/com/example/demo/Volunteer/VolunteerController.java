package com.example.demo.Volunteer;

import com.example.demo.Project.ProjectDTO;
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
 * Controller for volunteers
 *
 * @author Thorvas
 */
@RestController
@RequestMapping("/api/v1/volunteers")
public class VolunteerController {

    @Autowired
    private VolunteerService volunteerService;

    private final String RESOURCE_PATH_LINK = "resource-path";

    private Link rootLink() {
        String ROOT_LINK = "root";
        return linkTo(methodOn(VolunteerController.class)
                .getVolunteers()).withRel(ROOT_LINK);
    }

    /**
     * GET endpoint for volunteers. It retrieves list of all volunteers
     *
     * @return JSON response containing list of all volunteers
     */
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CollectionModel<VolunteerDTO>> getVolunteers() {

        List<VolunteerDTO> volunteerDTOs = volunteerService.searchVolunteers();

        Link selfLink = linkTo(methodOn(VolunteerController.class)
                .getVolunteers()).withRel(RESOURCE_PATH_LINK);

        CollectionModel<VolunteerDTO> resource = CollectionModel.of(volunteerDTOs, selfLink);

        return new ResponseEntity<>(resource, HttpStatus.OK);
    }

    /**
     * GET endpoint for volunteers. It retrieves volunteer based on id parameter
     *
     * @param id Long id value of retrieved volunteer
     * @return JSON response containing retrieved volunteer
     */
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<VolunteerDTO> getVolunteer(@PathVariable Long id) {

        VolunteerDTO volunteerDTO = volunteerService.searchVolunteer(id);

        volunteerDTO.add(rootLink());

        return new ResponseEntity<>(volunteerDTO, HttpStatus.OK);
    }

    /**
     * GET endpoint for projects associated with certain volunteer
     *
     * @param id Long id value of inspected volunteer
     * @return JSON response containing list of retrieved projects
     */
    @GetMapping(value = "/{id}/projects", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CollectionModel<ProjectDTO>> getProjects(@PathVariable Long id) {

        List<ProjectDTO> projectDTOs = volunteerService.getParticipatingProjects(id);

        Link selfLink = linkTo(methodOn(VolunteerController.class)
                .getProjects(id)).withRel(RESOURCE_PATH_LINK);

        CollectionModel<ProjectDTO> resource = CollectionModel.of(projectDTOs, selfLink, rootLink());

        return new ResponseEntity<>(resource, HttpStatus.OK);
    }

    /**
     * GET endpoint for projects that are owned by volunteer
     *
     * @param id Long id value of inspected volunteer
     * @return JSON response containing list of projects owned by volunteer
     */
    @GetMapping(value = "/{id}/projects/owned", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CollectionModel<ProjectDTO>> getOwnedProjects(@PathVariable Long id) {

        List<ProjectDTO> projectDTOs = volunteerService.getOwnedProjects(id);

        Link selfLink = linkTo(methodOn(VolunteerController.class)
                .getOwnedProjects(id)).withRel(RESOURCE_PATH_LINK);

        CollectionModel<ProjectDTO> resource = CollectionModel.of(projectDTOs, selfLink, rootLink());

        return new ResponseEntity<>(resource, HttpStatus.OK);
    }

    /**
     * PATCH endpoint for volunteers
     *
     * @param volunteer Long id value of updated volunteer
     * @return JSON response containing updated volunteer
     */
    @PatchMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<VolunteerDTO> updateVolunteer(@PathVariable Long id,
                                                        @RequestBody @Valid VolunteerDTO volunteer) {

        VolunteerDTO volunteerDTO = volunteerService.updateVolunteer(id, volunteer);

        Link resourceLink = linkTo(methodOn(VolunteerController.class).updateVolunteer(id, volunteer)).withRel(RESOURCE_PATH_LINK);

        volunteerDTO.add(rootLink(), resourceLink);

        return new ResponseEntity<>(volunteerDTO, HttpStatus.OK);
    }

    /**
     * DELETE endpoint for volunteers
     *
     * @param id Long id value of deleted volunteer
     * @return JSON response containing
     */
    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<VolunteerDTO> deleteEntity(@PathVariable Long id) {

        VolunteerDTO volunteerDTO = volunteerService.deleteVolunteer(id);

        Link resourceLink = linkTo(methodOn(VolunteerController.class).deleteEntity(id)).withRel(RESOURCE_PATH_LINK);

        volunteerDTO.add(rootLink(), resourceLink);

        return new ResponseEntity<>(volunteerDTO, HttpStatus.OK);
    }

    /**
     * POST endpoint for volunteer's interests
     *
     * @param interests List of String that contains interests of volunteer
     * @param id        Long id value of edited volunteer
     * @return JSON response containing updated volunteer
     */
    @PostMapping(value = "/{id}/interests", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<VolunteerDTO> addInterests(@Valid @RequestBody List<String> interests, @PathVariable Long id) {

        VolunteerDTO volunteerDTO = volunteerService.updateInterests(id, interests);

        Link resourceLink = linkTo(methodOn(VolunteerController.class).addInterests(interests, id)).withRel(RESOURCE_PATH_LINK);

        volunteerDTO.add(rootLink(), resourceLink);

        return new ResponseEntity<>(volunteerDTO, HttpStatus.OK);
    }
}
