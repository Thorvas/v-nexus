package com.example.demo.Controller;

import com.example.demo.DTO.OpinionDTO;
import com.example.demo.DTO.ProjectDTO;
import com.example.demo.DTO.VolunteerDTO;
import com.example.demo.Objects.CustomUserDetails;
import com.example.demo.Objects.Opinion;
import com.example.demo.Objects.Project;
import com.example.demo.Objects.Volunteer;
import com.example.demo.Mapper.OpinionMapper;
import com.example.demo.Mapper.ProjectMapper;
import com.example.demo.Mapper.VolunteerMapper;
import com.example.demo.Services.OpinionService;
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

@RestController
@RequestMapping("/api/v1/opinions")
public class OpinionController {

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

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<EntityModel<OpinionDTO>> getOpinion(@PathVariable Long id) {

        if (id != null) {

            Opinion foundOpinion = opinionService.searchOpinion(id).orElseThrow(() -> new EntityNotFoundException("Requested opinion could not be found."));

            OpinionDTO opinionDTO = opinionMapper.mapOpinionToDTO(foundOpinion);

            EntityModel<OpinionDTO> resource = EntityModel.of(opinionDTO);

            return new ResponseEntity<>(resource, HttpStatus.OK);
        } else {
            throw new IllegalArgumentException("Id of requested opinion cannot be null.");
        }
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CollectionModel<OpinionDTO>> getAllOpinions() {

        List<Opinion> opinions = opinionService.searchAllOpinions();

        List<OpinionDTO> opinionDTOs = opinions.stream()
                .map(opinionMapper::mapOpinionToDTO)
                .collect(Collectors.toList());

        CollectionModel<OpinionDTO> resource = CollectionModel.of(opinionDTOs);

        resource.add(linkTo(methodOn(OpinionController.class)
                .getAllOpinions()).withSelfRel());

        return new ResponseEntity<>(resource, HttpStatus.OK);
    }

    @GetMapping(value = "/{id}/project", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<EntityModel<ProjectDTO>> getProject(@PathVariable Long id) {

        if (id != null) {

            Opinion foundOpinion = opinionService.searchOpinion(id).orElseThrow(() -> new EntityNotFoundException("Requested opinion could not be found."));

            Project describedProject = foundOpinion.getDescribedProject();

            ProjectDTO projectDTO = projectMapper.mapProjectToDTO(describedProject);

            EntityModel<ProjectDTO> resource = EntityModel.of(projectDTO);

            return new ResponseEntity<>(resource, HttpStatus.OK);
        } else {
            throw new IllegalArgumentException("Id of requested opinion cannot be null.");
        }
    }

    @GetMapping(value = "/{id}/author", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<EntityModel<VolunteerDTO>> getAuthor(@PathVariable Long id) {

        if (id != null) {

            Opinion foundOpinion = opinionService.searchOpinion(id).orElseThrow(() -> new EntityNotFoundException("Requested opinion could not be found."));
            Volunteer author = foundOpinion.getAuthor();

            VolunteerDTO volunteerDTO = volunteerMapper.mapVolunteerToDTO(author);

            EntityModel<VolunteerDTO> resource = EntityModel.of(volunteerDTO);

            return new ResponseEntity<>(resource, HttpStatus.OK);
        } else {
            throw new IllegalArgumentException("Id of requested opinion cannot be null.");
        }
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<OpinionDTO> postOpinion(@RequestParam(name = "authorId") Long authorId,
                                                  @RequestParam(name = "projectId") Long projectId,
                                                  @RequestBody @Valid Opinion opinion,
                                                  Authentication principal) {

        CustomUserDetails userDetails = (CustomUserDetails) principal.getPrincipal();
        Volunteer loggedVolunteer = volunteerService.findVolunteer(userDetails.getUserData().getReferencedVolunteer().getId()).orElseThrow(() -> new EntityNotFoundException("Entity not found."));
        Volunteer author = volunteerService.findVolunteer(authorId).orElseThrow(() -> new EntityNotFoundException("Such author does not exist."));
        Project describedProject = projectService.findProject(projectId).orElseThrow(() -> new EntityNotFoundException("Described project does not exist."));

        if (loggedVolunteer.getId().equals(author.getId())) {

            opinion.setAuthor(author);
            opinion.setDescribedProject(describedProject);

            Opinion savedOpinion = opinionService.saveOpinion(opinion);
            OpinionDTO opinionDTO = opinionMapper.mapOpinionToDTO(savedOpinion);

            return new ResponseEntity<>(opinionDTO, HttpStatus.CREATED);
        }

        else {
            throw new BadCredentialsException("Logged user id does not match author's id.");
        }
    }
}
