package com.example.demo.Controller;

import com.example.demo.DTO.OpinionDTO;
import com.example.demo.DTO.ProjectDTO;
import com.example.demo.DTO.VolunteerDTO;
import com.example.demo.DummyObject.Opinion;
import com.example.demo.DummyObject.Project;
import com.example.demo.DummyObject.Volunteer;
import com.example.demo.Mapper.OpinionMapper;
import com.example.demo.Mapper.ProjectMapper;
import com.example.demo.Mapper.VolunteerMapper;
import com.example.demo.Services.OpinionService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
