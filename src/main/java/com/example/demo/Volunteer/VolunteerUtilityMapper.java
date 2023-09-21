package com.example.demo.Volunteer;

import com.example.demo.Project.Project;
import com.example.demo.Project.ProjectDTO;
import com.example.demo.Project.ProjectMapper;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class VolunteerUtilityMapper {

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private VolunteerMapper volunteerMapper;

    @Autowired
    private ProjectMapper projectMapper;

    public void mapDTOToVolunteer(VolunteerDTO volunteerDTO, Volunteer volunteer) {

        modelMapper.map(volunteerDTO, volunteer);
    }

    public VolunteerDTO mapVolunteerToDTO(Volunteer volunteer) {

        return this.volunteerMapper.mapVolunteerToDTO(volunteer);
    }

    public ProjectDTO mapProjectToDTO(Project project) {

        return this.projectMapper.mapProjectToDTO(project);
    }
}
