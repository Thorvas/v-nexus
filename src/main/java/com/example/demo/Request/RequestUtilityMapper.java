package com.example.demo.Request;

import com.example.demo.Project.Project;
import com.example.demo.Project.ProjectDTO;
import com.example.demo.Project.ProjectMapper;
import com.example.demo.Volunteer.Volunteer;
import com.example.demo.Volunteer.VolunteerDTO;
import com.example.demo.Volunteer.VolunteerMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RequestUtilityMapper {

    @Autowired
    private RequestMapper requestMapper;

    @Autowired
    private VolunteerMapper volunteerMapper;

    @Autowired
    private ProjectMapper projectMapper;

    public RequestDTO mapRequestToDTO(VolunteerRequest request) {

        return this.requestMapper.mapRequestToDTO(request);
    }

    public ProjectDTO mapProjectToDTO(Project project) {

        return this.projectMapper.mapProjectToDTO(project);
    }

    public VolunteerDTO mapVolunteerToDTO(Volunteer volunteer) {

        return this.volunteerMapper.mapVolunteerToDTO(volunteer);
    }
}
