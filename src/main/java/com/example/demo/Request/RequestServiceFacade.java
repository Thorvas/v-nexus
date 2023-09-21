package com.example.demo.Request;

import com.example.demo.Project.Project;
import com.example.demo.Project.ProjectDTO;
import com.example.demo.Project.ProjectRepository;
import com.example.demo.Project.ProjectService;
import com.example.demo.Volunteer.Volunteer;
import com.example.demo.Volunteer.VolunteerDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RequestServiceFacade {

    @Autowired
    private RequestAuthenticationManager requestAuthenticationManager;

    @Autowired
    private RequestUtilityMapper requestUtilityMapper;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ProjectService projectService;

    public Volunteer getLoggedVolunteer() {

        return this.requestAuthenticationManager.getLoggedVolunteer();
    }

    public boolean checkIfAdmin(Volunteer volunteer) {

        return this.requestAuthenticationManager.checkIfAdmin(volunteer);
    }

    public RequestDTO mapRequestToDTO(VolunteerRequest request) {

        return this.requestUtilityMapper.mapRequestToDTO(request);
    }

    public void saveProject(Project project) {


    }

    public boolean isProjectOpen(Project project) {

        return projectService.isProjectOpen(project);
    }

    public boolean isProjectFull(Project project) {

        return projectService.isProjectFull(project);
    }

    public VolunteerDTO mapVolunteerToDTO(Volunteer volunteer) {

        return this.requestUtilityMapper.mapVolunteerToDTO(volunteer);
    }

    public ProjectDTO mapProjectToDTO(Project project) {

        return this.requestUtilityMapper.mapProjectToDTO(project);
    }

    public Project findProject(Long projectId) {

        return projectService.findProject(projectId);
    }
}
