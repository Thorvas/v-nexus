package com.example.demo.Controller;

import com.example.demo.Objects.CustomUserDetails;
import com.example.demo.Objects.Project;
import com.example.demo.Objects.Volunteer;
import com.example.demo.Objects.VolunteerRequest;
import com.example.demo.Services.ProjectService;
import com.example.demo.Services.RequestService;
import com.example.demo.Services.VolunteerService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/requests")
public class RequestController {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private VolunteerService volunteerService;

    @Autowired
    private RequestService requestService;

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<VolunteerRequest> createRequest(@RequestParam("projectId") Long id, Authentication principal) {

        CustomUserDetails userDetails = (CustomUserDetails) principal.getPrincipal();
        Project foundProject = projectService.findProject(id).orElseThrow(() -> new EntityNotFoundException("Project not found."));
        Volunteer currentUser = volunteerService.findVolunteer(userDetails.getUserData().getReferencedVolunteer().getId()).orElseThrow(() -> new EntityNotFoundException("Entity not found."));
        Volunteer projectOwner = foundProject.getOwnerVolunteer();

        VolunteerRequest newRequest = VolunteerRequest.builder()
                .requestReceiver(projectOwner)
                .requestSender(currentUser)
                .requestedProject(foundProject)
                .build();

        requestService.saveRequest(newRequest);

        return new ResponseEntity<>(newRequest, HttpStatus.CREATED);

    }
}
