package com.example.demo.Opinion;

import com.example.demo.Authentication.AuthenticationService;
import com.example.demo.Volunteer.Volunteer;
import com.example.demo.Volunteer.VolunteerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OpinionAuthenticationManager {

    @Autowired
    private VolunteerService volunteerService;

    @Autowired
    private AuthenticationService authenticationService;

    public boolean checkIfAdmin(Volunteer volunteer) {

        return authenticationService.checkIfAdmin(volunteer);
    }

    public Volunteer getLoggedVolunteer() {

        return volunteerService.getLoggedVolunteer();
    }
}
