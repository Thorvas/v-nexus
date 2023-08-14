package com.example.demo.Opinion;

import com.example.demo.Authentication.AuthenticationService;
import com.example.demo.Project.Project;
import com.example.demo.Project.ProjectDTO;
import com.example.demo.Project.ProjectMapper;
import com.example.demo.Volunteer.Volunteer;
import com.example.demo.Volunteer.VolunteerDTO;
import com.example.demo.Volunteer.VolunteerMapper;
import com.example.demo.Volunteer.VolunteerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service responsible for opinion operations
 */
@Service
public class OpinionService {

    @Autowired
    OpinionRepository repository;

    @Autowired
    OpinionMapper opinionMapper;

    @Autowired
    ProjectMapper projectMapper;

    @Autowired
    VolunteerService volunteerService;

    @Autowired
    AuthenticationService authenticationService;

    @Autowired
    VolunteerMapper volunteerMapper;

    public Optional<Opinion> findOpinion(Long id) {

        return repository.findById(id);
    }

    /**
     * Searches for opinion based on id parameter
     *
     * @param id Id value of searched opinion
     * @return Optional containing result of search
     */
    public Optional<OpinionDTO> searchOpinion(Long id) {

        if (this.findOpinion(id).isPresent()) {

            Opinion opinion = this.findOpinion(id).get();
            OpinionDTO opinionDTO = opinionMapper.mapOpinionToDTO(opinion);

            return Optional.of(opinionDTO);
        }

        return Optional.empty();

    }

    /**
     * Searches for all opinions in database
     *
     * @return List of all found opinions
     */
    public List<OpinionDTO> searchAllOpinions() {

        List<Opinion> opinions = repository.findAll();

        return opinions.stream()
                .map(opinionMapper::mapOpinionToDTO)
                .toList();
    }

    public VolunteerDTO getAuthor(Opinion opinion) {

        Volunteer author = opinion.getAuthor();

        return volunteerMapper.mapVolunteerToDTO(author);
    }

    public ProjectDTO getDescribedProject(Opinion opinion) {

        Project describedProject = opinion.getDescribedProject();

        return projectMapper.mapProjectToDTO(describedProject);
    }

    /**
     * Deletes opinion
     *
     * @param opinion Deleted opinion object
     */
    public Optional<OpinionDTO> deleteOpinion(Opinion opinion) {

        if (this.isOpinionAuthor(opinion.getAuthor(), volunteerService.getLoggedVolunteer()) || authenticationService.checkIfAdmin(volunteerService.getLoggedVolunteer())) {

            repository.delete(opinion);

            return Optional.of(opinionMapper.mapOpinionToDTO(opinion));
        }

        return Optional.empty();


    }

    /**
     * Checks whether volunteer is author of opinion
     *
     * @param author First user compared to second one
     * @param user   Second user compared to first one
     * @return Boolean containing result of comparison
     */
    public boolean isOpinionAuthor(Volunteer author, Volunteer user) {

        return author.getId().equals(user.getId());
    }

    /**
     * Creates an opinion
     *
     * @param author     Volunteer object treated as author of opinion
     * @param project    Project object treated as described project
     * @param opinionDTO OpinionDTO containing content of opinion
     * @return Created opinion
     */
    public Optional<OpinionDTO> createOpinion(Volunteer author, Project project, OpinionDTO opinionDTO) {

        if (this.isOpinionAuthor(author, volunteerService.getLoggedVolunteer()) || authenticationService.checkIfAdmin(volunteerService.getLoggedVolunteer())) {

            Opinion opinion = Opinion.builder()
                    .opinion(opinionDTO.getContent())
                    .author(author)
                    .describedProject(project)
                    .build();

            repository.save(opinion);

            return Optional.of(opinionMapper.mapOpinionToDTO(opinion));
        }

        return Optional.empty();
    }
}
