package com.example.demo.Controller;

import com.example.demo.DTO.ProjectDTO;
import com.example.demo.DTO.RequestDTO;
import com.example.demo.DTO.VolunteerDTO;
import com.example.demo.Mapper.ProjectMapper;
import com.example.demo.Mapper.RequestMapper;
import com.example.demo.Mapper.VolunteerMapper;
import com.example.demo.Objects.Project;
import com.example.demo.Objects.Volunteer;
import com.example.demo.Objects.VolunteerRequest;
import com.example.demo.Services.AuthenticationService;
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
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * Controller for requests
 *
 * @author Thorvas
 */
@RestController
@RequestMapping("/api/v1/requests")
public class RequestController {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private VolunteerService volunteerService;

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private VolunteerMapper volunteerMapper;

    @Autowired
    private RequestService requestService;

    @Autowired
    private RequestMapper requestMapper;

    @Autowired
    private ProjectMapper projectMapper;

    private final String PERMISSION_DENIED_MESSAGE = "You are not permitted to perform this operation.";
    private final String VOLUNTEER_NOT_FOUND_MESSAGE = "Requested volunteer could not be found.";
    private final String PROJECT_NOT_FOUND_MESSAGE = "Requested project could not be found.";
    private final String REQUEST_NOT_FOUND_MESSAGE = "Request could not be found.";
    private final String ROOT_LINK = "root";

    /**
     * POST endpoint for requests. It allows volunteers to create requests for joining to projects
     *
     * @param id Long id value of project that volunteer wants to apply for
     * @return JSON response containing created request
     */
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RequestDTO> createRequest(@RequestParam("projectId") Long id) {

        Project foundProject = projectService.findProject(id).orElseThrow(() -> new EntityNotFoundException(PROJECT_NOT_FOUND_MESSAGE));
        Volunteer loggedUser = volunteerService.findVolunteer(volunteerService.getLoggedVolunteer().getId()).orElseThrow(() -> new EntityNotFoundException(VOLUNTEER_NOT_FOUND_MESSAGE));
        Volunteer projectOwner = foundProject.getOwnerVolunteer();

        VolunteerRequest newRequest = requestService.createRequest(projectOwner, loggedUser, foundProject);

        RequestDTO requestDTO = requestMapper.mapRequestToDTO(newRequest);

        return new ResponseEntity<>(requestDTO, HttpStatus.CREATED);

    }

    /**
     * GET endpoint for requests. It retrieves list of all stored requests
     *
     * @return JSON response containing retrieved requests
     */
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

    /**
     * GET endpoint for requests. It retrieves specific request based on id parameter
     *
     * @param id Long id value of returned request
     * @return JSON response containing returned request
     */
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RequestDTO> getSpecificRequest(@PathVariable Long id) {

        VolunteerRequest foundRequest = requestService.findRequest(id).orElseThrow(() -> new EntityNotFoundException(REQUEST_NOT_FOUND_MESSAGE));
        RequestDTO requestDTO = requestMapper.mapRequestToDTO(foundRequest);

        Link rootLink = linkTo(methodOn(RequestController.class)
                .getAllRequests()).withRel(ROOT_LINK);

        requestDTO.add(rootLink);

        return new ResponseEntity<>(requestDTO, HttpStatus.OK);
    }

    /**
     * DELETE endpoint for requests. It serves as emergency endpoint for administrators
     *
     * @param id Long id value of deleted request
     * @return JSON response containing deleted request
     */
    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RequestDTO> deleteRequest(@PathVariable Long id) {

        VolunteerRequest foundRequest = requestService.findRequest(id).orElseThrow(() -> new EntityNotFoundException(REQUEST_NOT_FOUND_MESSAGE));
        Volunteer loggedUser = volunteerService.findVolunteer(volunteerService.getLoggedVolunteer().getId()).orElseThrow(() -> new EntityNotFoundException(VOLUNTEER_NOT_FOUND_MESSAGE));

        if (authenticationService.checkIfAdmin(loggedUser)) {

            requestService.deleteRequest(foundRequest);

            RequestDTO requestDTO = requestMapper.mapRequestToDTO(foundRequest);

            return new ResponseEntity<>(requestDTO, HttpStatus.OK);
        } else {
            throw new AccessDeniedException(PERMISSION_DENIED_MESSAGE);
        }
    }

    /**
     * GET endpoint for volunteer that is sender of request. It can be accessed only by sender itself or receiver of request or administrator
     *
     * @param id Long id value of request that is inspected
     * @return JSON response containing volunteer that is assumed to be request sender
     */
    @GetMapping(value = "/{id}/sender", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<VolunteerDTO> getRequestSender(@PathVariable Long id) {

        VolunteerRequest foundRequest = requestService.findRequest(id).orElseThrow(() -> new EntityNotFoundException(REQUEST_NOT_FOUND_MESSAGE));
        Volunteer loggedUser = volunteerService.findVolunteer(volunteerService.getLoggedVolunteer().getId()).orElseThrow(() -> new EntityNotFoundException(VOLUNTEER_NOT_FOUND_MESSAGE));

        if (requestService.isVolunteerSenderOrReceiver(foundRequest, loggedUser) || authenticationService.checkIfAdmin(loggedUser)) {

            Volunteer requestSender = foundRequest.getRequestSender();

            Link rootLink = linkTo(methodOn(RequestController.class)
                    .getAllRequests()).withRel(ROOT_LINK);

            VolunteerDTO volunteerDTO = volunteerMapper.mapVolunteerToDTO(requestSender);

            volunteerDTO.add(rootLink);

            return new ResponseEntity<>(volunteerDTO, HttpStatus.OK);
        } else {
            throw new AccessDeniedException(PERMISSION_DENIED_MESSAGE);
        }


    }

    /**
     * GET endpoint for volunteer that is receiver of request. It can be accessed only by receiver itself or sender of request or administrator
     *
     * @param id Long id value of request that is inspected
     * @return JSON response containing volunteer that is assumed to be request receiver
     */
    @GetMapping(value = "/{id}/receiver", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<VolunteerDTO> getRequestReceiver(@PathVariable Long id) {

        Volunteer loggedUser = volunteerService.findVolunteer(volunteerService.getLoggedVolunteer().getId()).orElseThrow(() -> new EntityNotFoundException(VOLUNTEER_NOT_FOUND_MESSAGE));
        VolunteerRequest foundRequest = requestService.findRequest(id).orElseThrow(() -> new EntityNotFoundException(REQUEST_NOT_FOUND_MESSAGE));

        if (requestService.isVolunteerSenderOrReceiver(foundRequest, loggedUser) || authenticationService.checkIfAdmin(loggedUser)) {

            Volunteer requestReceiver = foundRequest.getRequestReceiver();

            Link rootLink = linkTo(methodOn(RequestController.class)
                    .getAllRequests()).withRel(ROOT_LINK);

            VolunteerDTO volunteerDTO = volunteerMapper.mapVolunteerToDTO(requestReceiver);

            volunteerDTO.add(rootLink);

            return new ResponseEntity<>(volunteerDTO, HttpStatus.OK);
        } else {
            throw new AccessDeniedException(PERMISSION_DENIED_MESSAGE);
        }
    }

    /**
     * GET endpoint for project that is associated with request
     *
     * @param id Long id value of request that is inspected
     * @return JSON response containing project associated with request
     */
    @GetMapping(value = "/{id}/project", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ProjectDTO> getRequestProject(@PathVariable Long id) {

        VolunteerRequest foundRequest = requestService.findRequest(id).orElseThrow(() -> new EntityNotFoundException(REQUEST_NOT_FOUND_MESSAGE));
        Project requestProject = foundRequest.getRequestedProject();

        Link rootLink = linkTo(methodOn(RequestController.class)
                .getAllRequests()).withRel(ROOT_LINK);

        ProjectDTO projectDTO = projectMapper.mapProjectToDTO(requestProject);

        projectDTO.add(rootLink);

        return new ResponseEntity<>(projectDTO, HttpStatus.OK);
    }

    /**
     * PATCH endpoint for accepting the request. It is accessible by receiver of request or administrator
     *
     * @param requestId Long id value of accepted request
     * @return JSON response containing accepted request
     */
    @PatchMapping(value = "/{id}/accept", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RequestDTO> acceptRequest(@PathVariable("id") Long requestId) {

        Volunteer loggedUser = volunteerService.findVolunteer(volunteerService.getLoggedVolunteer().getId()).orElseThrow(() -> new EntityNotFoundException(VOLUNTEER_NOT_FOUND_MESSAGE));
        VolunteerRequest request = requestService.findRequest(requestId).orElseThrow(() -> new EntityNotFoundException(REQUEST_NOT_FOUND_MESSAGE));

        Volunteer volunteerToAdd = request.getRequestSender();
        Project requestedProject = request.getRequestedProject();

        if ((requestService.isVolunteerReceiver(request, loggedUser) || authenticationService.checkIfAdmin(loggedUser)) && loggedUser.getOwnedProjects().contains(requestedProject)) {

            VolunteerRequest result = requestService.acceptRequest(request, volunteerToAdd, requestedProject).orElseThrow(() -> new AccessDeniedException(PERMISSION_DENIED_MESSAGE));

            RequestDTO requestDTO = requestMapper.mapRequestToDTO(result);

            return new ResponseEntity<>(requestDTO, HttpStatus.OK);
        }

        throw new AccessDeniedException(PERMISSION_DENIED_MESSAGE);
    }

    /**
     * PATCH endpoint for declining the request. It is accessible by receiver of request or administrator
     *
     * @param requestId Long id value of declined request
     * @return JSON response containing declined request
     */
    @PatchMapping(value = "/{id}/decline", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RequestDTO> declineRequest(@PathVariable("id") Long requestId) {

        Volunteer loggedUser = volunteerService.findVolunteer(volunteerService.getLoggedVolunteer().getId()).orElseThrow(() -> new EntityNotFoundException(VOLUNTEER_NOT_FOUND_MESSAGE));
        VolunteerRequest request = requestService.findRequest(requestId).orElseThrow(() -> new EntityNotFoundException(REQUEST_NOT_FOUND_MESSAGE));

        Project requestedProject = request.getRequestedProject();

        if ((requestService.isVolunteerReceiver(request, loggedUser) || authenticationService.checkIfAdmin(loggedUser)) && loggedUser.getOwnedProjects().contains(requestedProject) && requestService.hasPendingStatus(request)) {

            VolunteerRequest result = requestService.declineRequest(request).orElseThrow(() -> new AccessDeniedException(PERMISSION_DENIED_MESSAGE));

            RequestDTO requestDTO = requestMapper.mapRequestToDTO(result);

            return new ResponseEntity<>(requestDTO, HttpStatus.OK);
        } else {
            throw new AccessDeniedException(PERMISSION_DENIED_MESSAGE);
        }
    }
}

