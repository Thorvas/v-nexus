package com.example.demo.Request;

import com.example.demo.Project.ProjectDTO;
import com.example.demo.Project.ProjectService;
import com.example.demo.Volunteer.VolunteerDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * Controller for requests
 *
 * @author Thorvas
 */
@RestController
@Tag(name = "Requests")
@RequestMapping("/api/v1/requests")
public class RequestController {

    @Autowired
    private RequestService requestService;

    private final String RESOURCE_PATH_LINK = "resource-path";

    private Link rootLink() {
        String ROOT_LINK = "root";
        return linkTo(methodOn(RequestController.class)
                .getAllRequests()).withRel(ROOT_LINK);
    }

    /**
     * POST endpoint for requests. It allows volunteers to create requests for joining to projects
     *
     * @param id Long id value of project that volunteer wants to apply for
     * @return JSON response containing created request
     */
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Posts request", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<RequestDTO> createRequest(@RequestParam("projectId") Long id) {

        RequestDTO requestDTO = requestService.createRequest(id);

        Link resourceLink = linkTo(methodOn(RequestController.class)
                .createRequest(id)).withRel(RESOURCE_PATH_LINK);

        requestDTO.add(rootLink(), resourceLink);

        return new ResponseEntity<>(requestDTO, HttpStatus.CREATED);

    }

    /**
     * GET endpoint for requests. It retrieves list of all stored requests
     *
     * @return JSON response containing retrieved requests
     */
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Retrieves all requests", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<CollectionModel<RequestDTO>> getAllRequests() {

        List<RequestDTO> requestDTOs = requestService.searchAllRequests();

        Link selfLink = linkTo(methodOn(RequestController.class)
                .getAllRequests()).withRel(RESOURCE_PATH_LINK);

        CollectionModel<RequestDTO> resource = CollectionModel.of(requestDTOs, selfLink);

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
    @Operation(summary = "Retrieves specific request", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<RequestDTO> getSpecificRequest(@PathVariable Long id) {

        RequestDTO requestDTO = requestService.searchRequest(id);

        Link selfLink = linkTo(methodOn(RequestController.class)
                .getSpecificRequest(id)).withRel(RESOURCE_PATH_LINK);

        requestDTO.add(rootLink(), selfLink);

        return new ResponseEntity<>(requestDTO, HttpStatus.OK);
    }

    /**
     * DELETE endpoint for requests. It serves as emergency endpoint for administrators
     *
     * @param id Long id value of deleted request
     * @return JSON response containing deleted request
     */
    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Deletes request", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<RequestDTO> deleteRequest(@PathVariable Long id) {

        RequestDTO requestDTO = requestService.deleteRequest(id);
        Link selfLink = linkTo(methodOn(RequestController.class)
                .deleteRequest(id)).withRel(RESOURCE_PATH_LINK);

        requestDTO.add(rootLink(), selfLink);
        return new ResponseEntity<>(requestDTO, HttpStatus.OK);
    }

    /**
     * GET endpoint for volunteer that is sender of request. It can be accessed only by sender itself or receiver of request or administrator
     *
     * @param id Long id value of request that is inspected
     * @return JSON response containing volunteer that is assumed to be request sender
     */
    @GetMapping(value = "/{id}/sender", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Retrieves sender of request", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<VolunteerDTO> getRequestSender(@PathVariable Long id) {

        VolunteerDTO volunteerDTO = requestService.getRequestSender(id);
        Link selfLink = linkTo(methodOn(RequestController.class)
                .getRequestSender(id)).withRel(RESOURCE_PATH_LINK);

        volunteerDTO.add(rootLink(), selfLink);
        return new ResponseEntity<>(volunteerDTO, HttpStatus.OK);
    }

    /**
     * GET endpoint for volunteer that is receiver of request. It can be accessed only by receiver itself or sender of request or administrator
     *
     * @param id Long id value of request that is inspected
     * @return JSON response containing volunteer that is assumed to be request receiver
     */
    @GetMapping(value = "/{id}/receiver", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Retrieves receiver of request", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<VolunteerDTO> getRequestReceiver(@PathVariable Long id) {

        VolunteerDTO volunteerDTO = requestService.getRequestReceiver(id);
        Link selfLink = linkTo(methodOn(RequestController.class)
                .getRequestReceiver(id)).withRel(RESOURCE_PATH_LINK);

        volunteerDTO.add(rootLink(), selfLink);
        return new ResponseEntity<>(volunteerDTO, HttpStatus.OK);
    }

    /**
     * GET endpoint for project that is associated with request
     *
     * @param id Long id value of request that is inspected
     * @return JSON response containing project associated with request
     */
    @GetMapping(value = "/{id}/project", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Retrieves project associated with request", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ProjectDTO> getRequestProject(@PathVariable Long id) {

        ProjectDTO projectDTO = requestService.getRequestedProject(id);
        Link selfLink = linkTo(methodOn(RequestController.class)
                .getRequestProject(id)).withRel(RESOURCE_PATH_LINK);

        projectDTO.add(rootLink(), selfLink);
        return new ResponseEntity<>(projectDTO, HttpStatus.OK);
    }

    /**
     * PUT endpoint for accepting the request. It is accessible by receiver of request or administrator
     *
     * @param requestId Long id value of accepted request
     * @return JSON response containing accepted request
     */
    @PutMapping(value = "/{id}/accept", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Accepts request", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<RequestDTO> acceptRequest(@PathVariable("id") Long requestId) {

        RequestDTO requestDTO = requestService.acceptRequest(requestId);
        Link selfLink = linkTo(methodOn(RequestController.class)
                .acceptRequest(requestId)).withRel(RESOURCE_PATH_LINK);

        requestDTO.add(rootLink(), selfLink);
        return new ResponseEntity<>(requestDTO, HttpStatus.OK);
    }

    /**
     * PUT endpoint for declining the request. It is accessible by receiver of request or administrator
     *
     * @param requestId Long id value of declined request
     * @return JSON response containing declined request
     */
    @PutMapping(value = "/{id}/decline", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Declines request", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<RequestDTO> declineRequest(@PathVariable("id") Long requestId) {

        RequestDTO requestDTO = requestService.declineRequest(requestId);
        Link selfLink = linkTo(methodOn(RequestController.class)
                .declineRequest(requestId)).withRel(RESOURCE_PATH_LINK);

        requestDTO.add(rootLink(), selfLink);
        return new ResponseEntity<>(requestDTO, HttpStatus.OK);
    }
}

