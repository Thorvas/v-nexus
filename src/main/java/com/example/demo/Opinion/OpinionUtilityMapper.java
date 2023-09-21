package com.example.demo.Opinion;

import com.example.demo.Project.Project;
import com.example.demo.Project.ProjectDTO;
import com.example.demo.Project.ProjectMapper;
import com.example.demo.Volunteer.Volunteer;
import com.example.demo.Volunteer.VolunteerDTO;
import com.example.demo.Volunteer.VolunteerMapper;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OpinionUtilityMapper {

    @Autowired
    private OpinionMapper opinionMapper;

    @Autowired
    private ProjectMapper projectMapper;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private VolunteerMapper volunteerMapper;

    public OpinionDTO mapOpinionToDTO(Opinion opinion) {

        return this.opinionMapper.mapOpinionToDTO(opinion);
    }

    public ProjectDTO mapProjectToDTO(Project project) {

        return this.projectMapper.mapProjectToDTO(project);
    }

    public VolunteerDTO mapVolunteerToDTO(Volunteer volunteer) {

        return this.volunteerMapper.mapVolunteerToDTO(volunteer);
    }

    public void mapDTOToOpinion(OpinionDTO opinionDTO, Opinion opinion) {

        modelMapper.map(opinionDTO, opinion);
    }
}
