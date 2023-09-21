package com.example.demo.Volunteer;

import com.example.demo.Project.Project;
import com.example.demo.Project.ProjectDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class VolunteerServiceFacade {

    @Autowired
    private VolunteerUtilityMapper volunteerUtilityMapper;

    @Autowired
    private VolunteerAuthenticationManager volunteerAuthenticationManager;

    public void mapDTOToVolunteer(VolunteerDTO volunteerDTO, Volunteer volunteer) {

        this.volunteerUtilityMapper.mapDTOToVolunteer(volunteerDTO, volunteer);
    }

    public boolean checkIfAdmin(Volunteer volunteer) {

        return volunteerAuthenticationManager.checkIfAdmin(volunteer);
    }

    public VolunteerDTO mapVolunteerToDTO(Volunteer volunteer) {

        return this.volunteerUtilityMapper.mapVolunteerToDTO(volunteer);
    }

    public ProjectDTO mapProjectToDTO(Project project) {

        return this.volunteerUtilityMapper.mapProjectToDTO(project);
    }


}
