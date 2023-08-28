package com.example.demo.Opinion;

import com.example.demo.Authentication.AuthenticationService;
import com.example.demo.Error.InsufficientPermissionsException;
import com.example.demo.Error.OpinionNotFoundException;
import com.example.demo.Project.Project;
import com.example.demo.Project.ProjectDTO;
import com.example.demo.Project.ProjectMapper;
import com.example.demo.Project.ProjectService;
import com.example.demo.Volunteer.Volunteer;
import com.example.demo.Volunteer.VolunteerDTO;
import com.example.demo.Volunteer.VolunteerMapper;
import com.example.demo.Volunteer.VolunteerService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service responsible for opinion operations
 *
 * @author Thorvas
 */
@Service
public class OpinionService {

    @Autowired
    OpinionRepository repository;

    @Autowired
    ProjectService projectService;

    @Autowired
    OpinionMapper opinionMapper;

    @Autowired
    ProjectMapper projectMapper;

    @Autowired
    VolunteerService volunteerService;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    AuthenticationService authenticationService;

    @Autowired
    VolunteerMapper volunteerMapper;

    /**
     * Searches for opinion in database
     *
     * @param id Id value of searched opinion
     * @return Found opinion
     */
    public Opinion findOpinion(Long id) {

        if (repository.findById(id).isPresent()) {
            return repository.findById(id).get();
        }

        throw new OpinionNotFoundException("Requested opinion could not be found");
    }

    /**
     * Searches for opinion based on id parameter using utility method
     *
     * @param id Id value of searched opinion
     * @return Found opinion
     */
    public OpinionDTO searchOpinion(Long id) {

        Opinion opinion = this.findOpinion(id);

        return opinionMapper.mapOpinionToDTO(opinion);
    }

    /**
     * Searches for all opinions in database
     *
     * @return List of all found opinions
     */
    public List<OpinionDTO> searchAllOpinions() {

        List<Opinion> opinions = repository.findAll();

        return opinions.stream().map(opinionMapper::mapOpinionToDTO).toList();
    }

    /**
     * Retrieves author of opinion
     *
     * @param opinionId Id value of inspected opinion
     * @return Author of opinion
     */
    public VolunteerDTO getAuthor(Long opinionId) {

        Opinion opinion = this.findOpinion(opinionId);

        Volunteer author = opinion.getAuthor();

        return volunteerMapper.mapVolunteerToDTO(author);
    }

    public ProjectDTO getDescribedProject(Long opinionId) {

        Opinion opinion = this.findOpinion(opinionId);

        Project describedProject = opinion.getDescribedProject();

        return projectMapper.mapProjectToDTO(describedProject);
    }

    /**
     * Deletes opinion
     *
     * @param opinionId Id value of deleted opinion
     * @return Deleted opinion
     */
    public OpinionDTO deleteOpinion(Long opinionId) {

        Opinion opinion = this.findOpinion(opinionId);

        if (this.isOpinionAuthor(volunteerService.getLoggedVolunteer(), opinion) || authenticationService.checkIfAdmin(volunteerService.getLoggedVolunteer())) {

            repository.delete(opinion);

            return opinionMapper.mapOpinionToDTO(opinion);
        }

        throw new InsufficientPermissionsException("You cannot delete this opinion because you are not its author");


    }

    /**
     * Checks whether volunteer is author of opinion
     *
     * @param author  User compared to opinion's author
     * @param opinion Inspected opinion
     * @return Boolean containing result of comparison
     */
    public boolean isOpinionAuthor(Volunteer author, Opinion opinion) {

        return author.getId().equals(opinion.getAuthor().getId());
    }

    /**
     * Creates an opinion
     *
     * @param projectId  Project object treated as described project
     * @param opinionDTO OpinionDTO containing content of opinion
     * @return Created opinion
     */
    public OpinionDTO createOpinion(Long projectId, OpinionDTO opinionDTO) {

        Project project = projectService.findProject(projectId);
        Opinion opinion = Opinion.builder().opinion(opinionDTO.getContent()).author(volunteerService.getLoggedVolunteer()).describedProject(project).build();

        repository.save(opinion);

        return opinionMapper.mapOpinionToDTO(opinion);
    }

    /**
     * Updates an opinion
     *
     * @param opinionId  Long id value of updated opinion
     * @param opinionDTO OpinionDTO object containing updated data
     * @return JSON response containing updated DTO object
     */
    public OpinionDTO updateOpinion(Long opinionId, OpinionDTO opinionDTO) {

        Opinion updatedOpinion = this.findOpinion(opinionId);

        if (this.isOpinionAuthor(volunteerService.getLoggedVolunteer(), updatedOpinion) || authenticationService.checkIfAdmin(volunteerService.getLoggedVolunteer())) {

            modelMapper.map(opinionDTO, updatedOpinion);

            repository.save(updatedOpinion);

            return opinionMapper.mapOpinionToDTO(updatedOpinion);
        }

        throw new InsufficientPermissionsException("You are not permitted to modify this opinion content");
    }
}
