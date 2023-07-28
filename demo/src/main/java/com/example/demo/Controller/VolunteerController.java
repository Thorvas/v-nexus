package com.example.demo.Controller;

import com.example.demo.DTO.ProjectDTO;
import com.example.demo.DTO.VolunteerDTO;
import com.example.demo.Mapper.OpinionMapper;
import com.example.demo.Mapper.ProjectMapper;
import com.example.demo.Mapper.VolunteerMapper;
import com.example.demo.Objects.CustomUserDetails;
import com.example.demo.Objects.Volunteer;
import com.example.demo.Services.ProjectService;
import com.example.demo.Services.VolunteerService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * Controller for handling DummyEntity requests.
 *
 * @author Thorvas
 */
@RestController
@RequestMapping("/api/v1/volunteers")
public class VolunteerController {
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

    /**
     * Retrieves estimation data from database based on parameters provided for filtering.
     */
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CollectionModel<VolunteerDTO>> getVolunteers() {

        List<Volunteer> foundVolunteers = volunteerService.searchVolunteers();

        if (foundVolunteers != null) {

            List<VolunteerDTO> volunteerDTOs = foundVolunteers.stream()
                    .map(volunteerMapper::mapVolunteerToDTO)
                    .collect(Collectors.toList());

            CollectionModel<VolunteerDTO> resource = CollectionModel.of(volunteerDTOs);

            resource.add(linkTo(methodOn(VolunteerController.class)
                    .getVolunteers()).withSelfRel());

            return new ResponseEntity<>(resource, HttpStatus.OK);

        } else {

            throw new EntityNotFoundException("Requested volunteers could not be found");
        }
    }

    @GetMapping(value = "/{id}/matchingProjects", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CollectionModel<ProjectDTO>> getMatchingProjects(@PathVariable Long id) {

        Volunteer foundVolunteer = volunteerService.findVolunteer(id).orElseThrow(() -> new EntityNotFoundException("Volunteer of requested id could not be found."));

        List<ProjectDTO> projectDTOs = projectService.matchProjectsWithSkills(volunteerMapper.mapVolunteerToDTO(foundVolunteer))
                .stream()
                .map(projectMapper::mapProjectToDTO)
                .collect(Collectors.toList());

        CollectionModel<ProjectDTO> resource = CollectionModel.of(projectDTOs);

        resource.add(linkTo(methodOn(VolunteerController.class)
                        .getMatchingProjects(id)).withSelfRel(),
                linkTo(methodOn(VolunteerController.class)
                        .getVolunteers()).withRel("root"));

        return new ResponseEntity<>(resource, HttpStatus.OK);
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<EntityModel<VolunteerDTO>> getVolunteer(@PathVariable Long id) {

        Volunteer foundVolunteer = volunteerService.findVolunteer(id).orElseThrow(() -> new EntityNotFoundException("Volunteer of requested id could not be found."));

        VolunteerDTO volunteerDTO = volunteerMapper.mapVolunteerToDTO(foundVolunteer);

        EntityModel<VolunteerDTO> resource = EntityModel.of(volunteerDTO);

        return new ResponseEntity<>(resource, HttpStatus.OK);
    }

    @GetMapping(value = "/{id}/projects", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CollectionModel<ProjectDTO>> getProjects(@PathVariable Long id) {

        Volunteer foundVolunteer = volunteerService.findVolunteer(id).orElseThrow(() -> new EntityNotFoundException("Volunteer of requested id could not be found."));

        List<ProjectDTO> projectDTOs = foundVolunteer.getParticipatingProjects().stream()
                .map(projectMapper::mapProjectToDTO)
                .collect(Collectors.toList());

        CollectionModel<ProjectDTO> resource = CollectionModel.of(projectDTOs);

        resource.add(linkTo(methodOn(VolunteerController.class)
                        .getProjects(id)).withSelfRel(),
                linkTo(methodOn(VolunteerController.class)
                        .getVolunteers()).withRel("root"));

        return new ResponseEntity<>(resource, HttpStatus.OK);
    }

    //URL to change!
    @GetMapping(value = "/{id}/projects/owned", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CollectionModel<ProjectDTO>> getOwnedProjects(@PathVariable Long id) {

        Volunteer foundVolunteer = volunteerService.findVolunteer(id).orElseThrow(() -> new EntityNotFoundException("Volunteer of requested id could not be found."));

        List<ProjectDTO> projectDTOs = foundVolunteer.getOwnedProjects().stream()
                .map(projectMapper::mapProjectToDTO)
                .collect(Collectors.toList());

        CollectionModel<ProjectDTO> resource = CollectionModel.of(projectDTOs);

        resource.add(linkTo(methodOn(VolunteerController.class)
                        .getOwnedProjects(id)).withSelfRel(),
                linkTo(methodOn(VolunteerController.class)
                        .getVolunteers()).withRel("root"));

        return new ResponseEntity<>(resource, HttpStatus.OK);
    }

    /**
     * Updates an entity in database
     *
     * @param volunteer An ID value of updated object
     * @return The ResponseEntity object containing updated object
     */
    @PatchMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<VolunteerDTO> updateVolunteer(@RequestBody @Valid Volunteer volunteer, Authentication principal) {

        CustomUserDetails userDetails = (CustomUserDetails) principal.getPrincipal();
        Volunteer foundVolunteer = volunteerService.findVolunteer(userDetails.getUserData().getReferencedVolunteer().getId()).orElseThrow(() -> new EntityNotFoundException("Entity not found."));
        volunteer.setId(foundVolunteer.getId());

        if (volunteer != null) {
            if (foundVolunteer.getUserData() != null && principal.getName().equals(foundVolunteer.getUserData().getUsername())) {

                Volunteer savedVolunteer = volunteerService.saveVolunteer(volunteer);

                VolunteerDTO returnedDTO = volunteerMapper.mapVolunteerToDTO(savedVolunteer);

                return new ResponseEntity<>(returnedDTO, HttpStatus.OK);

            } else {
                throw new BadCredentialsException("You cannot edit other volunteer's data.");
            }
        } else {
            throw new IllegalArgumentException("Updated volunteer cannot be null");
        }
    }

    /**
     * Deletes an entity from database
     *
     * @param id An ID value of deleted object
     * @return The String with deletion message
     */
    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<VolunteerDTO> deleteEntity(@PathVariable Long id) {

        if (id != null) {

            Volunteer volunteerToDelete = volunteerService.findVolunteer(id).orElseThrow(() -> new EntityNotFoundException("Volunteer to delete could not be found."));
            VolunteerDTO returnedDTO = volunteerMapper.mapVolunteerToDTO(volunteerToDelete);

            volunteerService.deleteVolunteer(volunteerToDelete);

            return new ResponseEntity<>(returnedDTO, HttpStatus.OK);
        } else {

            throw new IllegalArgumentException("An ID of requested volunteer to patch cannot be null.");
        }


    }
}
