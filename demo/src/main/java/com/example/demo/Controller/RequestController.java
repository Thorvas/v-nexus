package com.example.demo.Controller;

import com.example.demo.DTO.ProjectDTO;
import com.example.demo.DTO.RequestDTO;
import com.example.demo.DTO.VolunteerDTO;
import com.example.demo.Mapper.ProjectMapper;
import com.example.demo.Mapper.RequestMapper;
import com.example.demo.Mapper.VolunteerMapper;
import com.example.demo.Objects.*;
import com.example.demo.Services.ProjectService;
import com.example.demo.Services.RequestService;
import com.example.demo.Services.VolunteerService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/v1/requests")
public class RequestController {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private VolunteerService volunteerService;

    @Autowired
    private VolunteerMapper volunteerMapper;

    @Autowired
    private RequestService requestService;

    @Autowired
    private RequestMapper requestMapper;

    @Autowired
    private ProjectMapper projectMapper;


    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RequestDTO> createRequest(@RequestParam("projectId") Long id, @AuthenticationPrincipal CustomUserDetails userDetails) {

        Project foundProject = projectService.findProject(id).orElseThrow(() -> new EntityNotFoundException("Project not found."));
        Volunteer currentUser = volunteerService.findVolunteer(userDetails.getUserData().getReferencedVolunteer().getId()).orElseThrow(() -> new EntityNotFoundException("Entity not found."));
        Volunteer projectOwner = foundProject.getOwnerVolunteer();

        VolunteerRequest newRequest = VolunteerRequest.builder()
                .requestReceiver(projectOwner)
                .requestSender(currentUser)
                .requestedProject(foundProject)
                .status(RequestStatus.PENDING)
                .build();

        requestService.saveRequest(newRequest);

        RequestDTO requestDTO = requestMapper.mapRequestToDTO(newRequest);

        return new ResponseEntity<>(requestDTO, HttpStatus.CREATED);

    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CollectionModel<RequestDTO>> getAllRequests() {

        List<VolunteerRequest> allRequests = requestService.searchAllRequests();

        List<RequestDTO> allRequestsDTO = allRequests.stream()
                .map(request -> requestMapper.mapRequestToDTO(request))
                .collect(Collectors.toList());

        Link selfLink = linkTo(methodOn(RequestController.class)
                .getAllRequests()).withSelfRel();

        CollectionModel<RequestDTO> resource = CollectionModel.of(allRequestsDTO);

        resource.add(selfLink);

        return new ResponseEntity<>(resource, HttpStatus.OK);
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RequestDTO> getSpecificRequest(@PathVariable Long id) {

        VolunteerRequest foundRequest = requestService.findRequest(id).orElseThrow(() -> new EntityNotFoundException("Request could not be found."));
        RequestDTO requestDTO = requestMapper.mapRequestToDTO(foundRequest);

        Link rootLink = linkTo(methodOn(RequestController.class)
                .getAllRequests()).withRel("root");

        requestDTO.add(rootLink);

        return new ResponseEntity<>(requestDTO, HttpStatus.OK);
    }


    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RequestDTO> deleteRequest(@PathVariable Long id) {

        VolunteerRequest foundRequest = requestService.findRequest(id).orElseThrow(() -> new EntityNotFoundException("Request could not be found."));

        requestService.deleteRequest(foundRequest);

        RequestDTO requestDTO = requestMapper.mapRequestToDTO(foundRequest);

        return new ResponseEntity<>(requestDTO, HttpStatus.OK);

    }

    @GetMapping(value = "/{id}/sender", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<VolunteerDTO> getRequestSender(@PathVariable Long id) {

        VolunteerRequest foundRequest = requestService.findRequest(id).orElseThrow(() -> new EntityNotFoundException("Request could not be found."));
        Volunteer requestSender = foundRequest.getRequestSender();

        Link rootLink = linkTo(methodOn(RequestController.class)
                .getAllRequests()).withRel("root");

        VolunteerDTO volunteerDTO = volunteerMapper.mapVolunteerToDTO(requestSender);

        volunteerDTO.add(rootLink);

        return new ResponseEntity<>(volunteerDTO, HttpStatus.OK);

    }

    @GetMapping(value = "/{id}/receiver", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<VolunteerDTO> getRequestReceiver(@PathVariable Long id, @AuthenticationPrincipal CustomUserDetails userDetails) {


        VolunteerRequest foundRequest = requestService.findRequest(id).orElseThrow(() -> new EntityNotFoundException("Request could not be found."));
        Volunteer requestReceiver = foundRequest.getRequestReceiver();

        Link rootLink = linkTo(methodOn(RequestController.class)
                .getAllRequests()).withRel("root");

        VolunteerDTO volunteerDTO = volunteerMapper.mapVolunteerToDTO(requestReceiver);

        volunteerDTO.add(rootLink);

        return new ResponseEntity<>(volunteerDTO, HttpStatus.OK);
    }

    @GetMapping(value = "/{id}/project", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ProjectDTO> getRequestProject(@PathVariable Long id) {

        VolunteerRequest foundRequest = requestService.findRequest(id).orElseThrow(() -> new EntityNotFoundException("Request could not be found."));
        Project requestProject = foundRequest.getRequestedProject();

        Link rootLink = linkTo(methodOn(RequestController.class)
                .getAllRequests()).withRel("root");

        ProjectDTO projectDTO = projectMapper.mapProjectToDTO(requestProject);

        projectDTO.add(rootLink);

        return new ResponseEntity<>(projectDTO, HttpStatus.OK);
    }

    @PatchMapping(value = "/{id}/accept", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RequestDTO> acceptRequest(@PathVariable("id") Long requestId, @AuthenticationPrincipal CustomUserDetails userDetails) {

        Volunteer loggedUser = volunteerService.findVolunteer(userDetails.getUserData().getReferencedVolunteer().getId()).orElseThrow(() -> new EntityNotFoundException("Entity not found."));
        VolunteerRequest request = requestService.findRequest(requestId).orElseThrow(() -> new EntityNotFoundException("Request was not found."));

        Volunteer volunteerToAdd = request.getRequestSender();
        Project requestedProject = request.getRequestedProject();

        if (request.getRequestReceiver().getId().equals(loggedUser.getId()) && loggedUser.getOwnedProjects().contains(requestedProject) && request.getStatus() == RequestStatus.PENDING) {

            requestedProject.addVolunteerToProject(volunteerToAdd);

            request.setStatus(RequestStatus.ACCEPTED);

            requestService.saveRequest(request);
            projectService.saveProject(requestedProject);

            RequestDTO requestDTO = requestMapper.mapRequestToDTO(request);

            return new ResponseEntity<>(requestDTO, HttpStatus.OK);
        }

        throw new IllegalArgumentException("You are not an owner of project detailed in request or request is not active.");
    }

    @PatchMapping(value = "/{id}/decline", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RequestDTO> declineRequest(@PathVariable("id") Long requestId, @AuthenticationPrincipal CustomUserDetails userDetails) {

        Volunteer loggedUser = volunteerService.findVolunteer(userDetails.getUserData().getReferencedVolunteer().getId()).orElseThrow(() -> new EntityNotFoundException("Entity not found."));
        VolunteerRequest request = requestService.findRequest(requestId).orElseThrow(() -> new EntityNotFoundException("Request was not found."));

        Volunteer volunteerToAdd = request.getRequestSender();
        Project requestedProject = request.getRequestedProject();

        if (request.getRequestReceiver().getId().equals(loggedUser.getId()) && loggedUser.getOwnedProjects().contains(requestedProject) && request.getStatus() == RequestStatus.PENDING) {

            request.setStatus(RequestStatus.DECLINED);
            requestService.saveRequest(request);

            RequestDTO requestDTO = requestMapper.mapRequestToDTO(request);

            return new ResponseEntity<>(requestDTO, HttpStatus.OK);
        } else {
            throw new IllegalArgumentException("You are not an owner of project detailed in request or request is not active.");
        }
    }
}

