package com.example.demo.Services;

import com.example.demo.DTO.OpinionDTO;
import com.example.demo.Objects.Opinion;
import com.example.demo.Objects.Project;
import com.example.demo.Objects.Volunteer;
import com.example.demo.Repositories.OpinionRepository;
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

    /**
     * Searches for opinion based on id parameter
     *
     * @param id Id value of searched opinion
     * @return Optional containing result of search
     */
    public Optional<Opinion> searchOpinion(Long id) {

        return repository.findById(id);
    }

    /**
     * Searches for all opinions in database
     *
     * @return List of all found opinions
     */
    public List<Opinion> searchAllOpinions() {

        return repository.findAll();
    }

    /**
     * Deletes opinion
     *
     * @param opinion Deleted opinion object
     */
    public void deleteOpinion(Opinion opinion) {

        repository.delete(opinion);
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
    public Opinion createOpinion(Volunteer author, Project project, OpinionDTO opinionDTO) {

        Opinion opinion = Opinion.builder()
                .opinion(opinionDTO.getContent())
                .author(author)
                .describedProject(project)
                .build();

        repository.save(opinion);

        return opinion;
    }
}
