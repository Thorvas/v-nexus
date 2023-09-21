package com.example.demo.Project;

import com.example.demo.Authentication.AuthenticationService;
import com.example.demo.Volunteer.Volunteer;
import com.example.demo.Volunteer.VolunteerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ProjectVolunteerManager {

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private VolunteerService volunteerService;

    public boolean checkIfAdmin(Volunteer volunteer) {

        return authenticationService.checkIfAdmin(volunteer);
    }

    public Volunteer findVolunteer(Long volunteerId) {

        return volunteerService.findVolunteer(volunteerId);
    }

    public Volunteer getLoggedVolunteer() {

        return this.volunteerService.getLoggedVolunteer();
    }
}
