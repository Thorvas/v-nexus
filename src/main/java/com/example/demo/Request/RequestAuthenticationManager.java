package com.example.demo.Request;

import com.example.demo.Authentication.AuthenticationService;
import com.example.demo.Volunteer.Volunteer;
import com.example.demo.Volunteer.VolunteerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RequestAuthenticationManager {

    @Autowired
    private VolunteerService volunteerService;

    @Autowired
    private AuthenticationService authenticationService;

    public Volunteer getLoggedVolunteer() {

        return this.volunteerService.getLoggedVolunteer();
    }

    public boolean checkIfAdmin(Volunteer volunteer) {

        return this.authenticationService.checkIfAdmin(volunteer);
    }
}
