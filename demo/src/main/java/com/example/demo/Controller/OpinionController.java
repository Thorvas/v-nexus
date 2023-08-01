package com.example.demo.Controller;

import com.example.demo.DTO.OpinionDTO;
import com.example.demo.DTO.ProjectDTO;
import com.example.demo.DTO.VolunteerDTO;
import com.example.demo.Mapper.OpinionMapper;
import com.example.demo.Mapper.ProjectMapper;
import com.example.demo.Mapper.VolunteerMapper;
import com.example.demo.Objects.CustomUserDetails;
import com.example.demo.Objects.Opinion;
import com.example.demo.Objects.Project;
import com.example.demo.Objects.Volunteer;
import com.example.demo.Services.OpinionService;
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
    public ResponseEntity<OpinionDTO> getOpinion(@PathVariable Long id) {

        Opinion foundOpinion = opinionService.searchOpinion(id).orElseThrow(() -> new EntityNotFoundException("Requested opinion could not be found."));

        OpinionDTO opinionDTO = opinionMapper.mapOpinionToDTO(foundOpinion);

        Link rootLink = linkTo(methodOn(OpinionController.class)
                .getAllOpinions()).withRel("root");

        opinionDTO.add(rootLink);

        return new ResponseEntity<>(opinionDTO, HttpStatus.OK);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CollectionModel<OpinionDTO>> getAllOpinions() {

        List<Opinion> opinions = opinionService.searchAllOpinions();

        List<OpinionDTO> opinionDTOs = opinions.stream()
                .map(opinionMapper::mapOpinionToDTO)
                .collect(Collectors.toList());

        Link selfLink = linkTo(methodOn(OpinionController.class)
                .getAllOpinions()).withSelfRel();

        CollectionModel<OpinionDTO> resource = CollectionModel.of(opinionDTOs, selfLink);

        return new ResponseEntity<>(resource, HttpStatus.OK);
    }

    @GetMapping(value = "/{id}/project", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ProjectDTO> getProject(@PathVariable Long id) {

        Opinion foundOpinion = opinionService.searchOpinion(id).orElseThrow(() -> new EntityNotFoundException("Requested opinion could not be found."));

        Project describedProject = foundOpinion.getDescribedProject();

        ProjectDTO projectDTO = projectMapper.mapProjectToDTO(describedProject);

        Link rootLink = linkTo(methodOn(ProjectController.class)
                .listProjects()).withRel("root");

        projectDTO.add(rootLink);

        return new ResponseEntity<>(projectDTO, HttpStatus.OK);
    }

    @GetMapping(value = "/{id}/author", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<VolunteerDTO> getAuthor(@PathVariable Long id) {

        Opinion foundOpinion = opinionService.searchOpinion(id).orElseThrow(() -> new EntityNotFoundException("Requested opinion could not be found."));
        Volunteer author = foundOpinion.getAuthor();

        VolunteerDTO volunteerDTO = volunteerMapper.mapVolunteerToDTO(author);

        Link rootLink = linkTo(methodOn(VolunteerController.class)
                .getVolunteers()).withRel("root");

        volunteerDTO.add(rootLink);

        return new ResponseEntity<>(volunteerDTO, HttpStatus.OK);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<OpinionDTO> postOpinion(@RequestParam(name = "authorId") Long authorId,
                                                  @RequestParam(name = "projectId") Long projectId,
                                                  @RequestBody @Valid Opinion opinion,
                                                  @AuthenticationPrincipal CustomUserDetails userDetails) {

        Volunteer loggedUser = volunteerService.findVolunteer(userDetails.getUserData().getReferencedVolunteer().getId()).orElseThrow(() -> new EntityNotFoundException("Entity not found."));
        Volunteer author = volunteerService.findVolunteer(authorId).orElseThrow(() -> new EntityNotFoundException("Such author does not exist."));
        Project describedProject = projectService.findProject(projectId).orElseThrow(() -> new EntityNotFoundException("Described project does not exist."));

        if (opinionService.isOpinionAuthor(author, loggedUser)) {

            Opinion savedOpinion = opinionService.createOpinion(author, describedProject, opinion.getOpinion());

            OpinionDTO opinionDTO = opinionMapper.mapOpinionToDTO(savedOpinion);

            return new ResponseEntity<>(opinionDTO, HttpStatus.CREATED);
        } else {
            throw new BadCredentialsException("Logged user id does not match author's id.");
        }
    }

    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<OpinionDTO> deleteOpinion(@PathVariable Long id, @AuthenticationPrincipal CustomUserDetails userDetails) {

        Opinion opinion = opinionService.searchOpinion(id).orElseThrow(() -> new EntityNotFoundException("Requested opinion could not be found."));
        Volunteer loggedUser = volunteerService.findVolunteer(userDetails.getUserData().getReferencedVolunteer().getId()).orElseThrow(() -> new EntityNotFoundException("Entity not found."));

        if (opinionService.isOpinionAuthor(opinion.getAuthor(), loggedUser) || loggedUser.getUserData().isAdmin()) {

            opinionService.deleteOpinion(opinion);

            OpinionDTO opinionDTO = opinionMapper.mapOpinionToDTO(opinion);

            return new ResponseEntity<>(opinionDTO, HttpStatus.OK);
        } else {
            throw new BadCredentialsException("You are not permitted to perform this operation.");
        }
    }
}
