package com.example.demo.Volunteer;

import com.example.demo.Project.ProjectDTO;
import com.example.demo.Opinion.OpinionMapper;
import com.example.demo.Project.ProjectMapper;
import com.example.demo.Authentication.AuthenticationService;
import com.example.demo.Project.ProjectService;
import com.example.demo.Request.RequestService;
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
import java.util.stream.Collectors;

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
    private final String PERMISSION_DENIED_MESSAGE = "You are not permitted to perform this operation.";
    private final String VOLUNTEER_NOT_FOUND_MESSAGE = "Requested volunteer could not be found.";
    private final String ROOT_LINK = "root";

    @Autowired
    private AuthenticationService authenticationService;
    @Autowired
    private VolunteerService volunteerService;
    @Autowired
    private ProjectService projectService;
    @Autowired
    private VolunteerMapper volunteerMapper;
    @Autowired
    private ProjectMapper projectMapper;
    @Autowired
    private OpinionMapper opinionMapper;
    @Autowired
    private RequestService requestService;

    /**
     * GET endpoint for volunteers. It retrieves list of all volunteers
     *
     * @return JSON response containing list of all volunteers
     */
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CollectionModel<VolunteerDTO>> getVolunteers() {

        List<Volunteer> foundVolunteers = volunteerService.searchVolunteers();

        if (foundVolunteers != null) {

            List<VolunteerDTO> volunteerDTOs = foundVolunteers.stream()
                    .map(volunteerMapper::mapVolunteerToDTO)
                    .collect(Collectors.toList());

            Link selfLink = linkTo(methodOn(VolunteerController.class)
                    .getVolunteers()).withSelfRel();

            CollectionModel<VolunteerDTO> resource = CollectionModel.of(volunteerDTOs, selfLink);

            return new ResponseEntity<>(resource, HttpStatus.OK);

        } else {

            throw new EntityNotFoundException(VOLUNTEER_NOT_FOUND_MESSAGE);
        }
    }

    /**
     * GET endpoint for volunteers. It retrieves volunteer based on id parameter
     *
     * @param id Long id value of retrieved volunteer
     * @return JSON response containing retrieved volunteer
     */
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<VolunteerDTO> getVolunteer(@PathVariable Long id) {

        Volunteer foundVolunteer = volunteerService.findVolunteer(id).orElseThrow(() -> new EntityNotFoundException(VOLUNTEER_NOT_FOUND_MESSAGE));

        Link rootLink = linkTo(methodOn(VolunteerController.class)
                .getVolunteers()).withRel(ROOT_LINK);

        VolunteerDTO volunteerDTO = volunteerMapper.mapVolunteerToDTO(foundVolunteer);

        volunteerDTO.add(rootLink);

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

        Volunteer foundVolunteer = volunteerService.findVolunteer(id).orElseThrow(() -> new EntityNotFoundException(VOLUNTEER_NOT_FOUND_MESSAGE));

        List<ProjectDTO> projectDTOs = foundVolunteer.getParticipatingProjects().stream()
                .map(projectMapper::mapProjectToDTO)
                .collect(Collectors.toList());

        Link selfLink = linkTo(methodOn(VolunteerController.class)
                .getProjects(id)).withSelfRel();

        Link rootLink = linkTo(methodOn(VolunteerController.class)
                .getVolunteers()).withRel(ROOT_LINK);

        CollectionModel<ProjectDTO> resource = CollectionModel.of(projectDTOs, selfLink, rootLink);

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

        Volunteer foundVolunteer = volunteerService.findVolunteer(id).orElseThrow(() -> new EntityNotFoundException(VOLUNTEER_NOT_FOUND_MESSAGE));

        List<ProjectDTO> projectDTOs = foundVolunteer.getOwnedProjects().stream()
                .map(projectMapper::mapProjectToDTO)
                .collect(Collectors.toList());

        Link selfLink = linkTo(methodOn(VolunteerController.class)
                .getOwnedProjects(id)).withSelfRel();

        Link rootLink = linkTo(methodOn(VolunteerController.class)
                .getVolunteers()).withRel(ROOT_LINK);

        CollectionModel<ProjectDTO> resource = CollectionModel.of(projectDTOs, selfLink, rootLink);

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

        Volunteer loggedUser = volunteerService.findVolunteer(volunteerService.getLoggedVolunteer().getId()).orElseThrow(() -> new EntityNotFoundException(VOLUNTEER_NOT_FOUND_MESSAGE));
        Volunteer updatedVolunteer = volunteerService.findVolunteer(id).orElseThrow(() -> new EntityNotFoundException(VOLUNTEER_NOT_FOUND_MESSAGE));

        if (volunteerService.isMatchingVolunteer(updatedVolunteer, loggedUser) || authenticationService.checkIfAdmin(loggedUser)) {

            Link rootLink = linkTo(methodOn(VolunteerController.class).getVolunteers()).withRel(ROOT_LINK);
            Volunteer savedVolunteer = volunteerService.updateVolunteer(updatedVolunteer, volunteer);
            VolunteerDTO volunteerDTO = volunteerMapper.mapVolunteerToDTO(savedVolunteer);

            volunteerDTO.add(rootLink);

            return new ResponseEntity<>(volunteerDTO, HttpStatus.OK);
        } else {

            throw new AccessDeniedException(PERMISSION_DENIED_MESSAGE);
        }
    }

    /**
     * DELETE endpoint for volunteers
     *
     * @param id Long id value of deleted volunteer
     * @return JSON response containing
     */
    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<VolunteerDTO> deleteEntity(@PathVariable Long id) {

        Volunteer loggedUser = volunteerService.findVolunteer(volunteerService.getLoggedVolunteer().getId()).orElseThrow(() -> new EntityNotFoundException(VOLUNTEER_NOT_FOUND_MESSAGE));
        Volunteer volunteerToDelete = volunteerService.findVolunteer(id).orElseThrow(() -> new EntityNotFoundException(VOLUNTEER_NOT_FOUND_MESSAGE));

        if (volunteerService.isMatchingVolunteer(loggedUser, volunteerToDelete) || authenticationService.checkIfAdmin(loggedUser)) {

            Link rootLink = linkTo(methodOn(VolunteerController.class).getVolunteers()).withRel(ROOT_LINK);
            VolunteerDTO volunteerDTO = volunteerMapper.mapVolunteerToDTO(volunteerToDelete);

            volunteerService.deleteVolunteer(volunteerToDelete);
            volunteerDTO.add(rootLink);

            return new ResponseEntity<>(volunteerDTO, HttpStatus.OK);
        } else {
            throw new AccessDeniedException(PERMISSION_DENIED_MESSAGE);
        }
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

        Volunteer loggedUser = volunteerService.findVolunteer(volunteerService.getLoggedVolunteer().getId()).orElseThrow(() -> new EntityNotFoundException(VOLUNTEER_NOT_FOUND_MESSAGE));
        Volunteer volunteerToEdit = volunteerService.findVolunteer(id).orElseThrow(() -> new EntityNotFoundException(VOLUNTEER_NOT_FOUND_MESSAGE));

        if (volunteerService.isMatchingVolunteer(loggedUser, volunteerToEdit) || authenticationService.checkIfAdmin(loggedUser)) {

            Link rootLink = linkTo(methodOn(VolunteerController.class).getVolunteers()).withRel(ROOT_LINK);

            volunteerService.updateInterests(volunteerToEdit, interests);
            VolunteerDTO volunteerDTO = volunteerMapper.mapVolunteerToDTO(volunteerToEdit);

            volunteerDTO.add(rootLink);

            return new ResponseEntity<>(volunteerDTO, HttpStatus.OK);
        } else {
            throw new AccessDeniedException(PERMISSION_DENIED_MESSAGE);
        }
    }
}
