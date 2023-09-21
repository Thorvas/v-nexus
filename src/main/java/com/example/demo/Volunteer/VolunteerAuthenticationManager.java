package com.example.demo.Volunteer;

import com.example.demo.Authentication.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class VolunteerAuthenticationManager {

    @Autowired
    private AuthenticationService authenticationService;

    public boolean checkIfAdmin(Volunteer volunteer) {

        return authenticationService.checkIfAdmin(volunteer);
    }
}
