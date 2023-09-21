package com.example.demo.Opinion;

import com.example.demo.Project.Project;
import com.example.demo.Project.ProjectDTO;
import com.example.demo.Project.ProjectService;
import com.example.demo.Volunteer.Volunteer;
import com.example.demo.Volunteer.VolunteerDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OpinionServiceFacade {

    @Autowired
    private OpinionAuthenticationManager opinionAuthenticationManager;

    @Autowired
    OpinionUtilityMapper opinionUtilityMapper;

    @Autowired
    ProjectService projectService;

    public boolean checkIfAdmin(Volunteer volunteer) {

        return opinionAuthenticationManager.checkIfAdmin(volunteer);
    }

    public Project findProject(Long projectId) {

        return this.projectService.findProject(projectId);
    }

    public Volunteer getLoggedVolunteer() {

        return opinionAuthenticationManager.getLoggedVolunteer();
    }

    public OpinionDTO mapOpinionToDTO(Opinion opinion) {

        return this.opinionUtilityMapper.mapOpinionToDTO(opinion);
    }

    public ProjectDTO mapProjectToDTO(Project project) {

        return this.opinionUtilityMapper.mapProjectToDTO(project);
    }

    public VolunteerDTO mapVolunteerToDTO(Volunteer volunteer) {

        return this.opinionUtilityMapper.mapVolunteerToDTO(volunteer);
    }

    public void mapDTOToOpinion(OpinionDTO opinionDTO, Opinion opinion) {

        opinionUtilityMapper.mapDTOToOpinion(opinionDTO, opinion);
    }

}
