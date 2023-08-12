package com.example.demo.Request;

import com.example.demo.Authentication.AuthenticationService;
import com.example.demo.Project.Project;
import com.example.demo.Project.ProjectDTO;
import com.example.demo.Project.ProjectMapper;
import com.example.demo.Project.ProjectRepository;
import com.example.demo.Volunteer.Volunteer;
import com.example.demo.Volunteer.VolunteerDTO;
import com.example.demo.Volunteer.VolunteerMapper;
import com.example.demo.Volunteer.VolunteerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service responsible for request operations
 */
@Service
public class RequestService {

    @Autowired
    private RequestRepository requestRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private VolunteerService volunteerService;

    @Autowired
    private RequestMapper requestMapper;

    @Autowired
    private VolunteerMapper volunteerMapper;

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private ProjectMapper projectMapper;


    /**
     * Searches for requests
     *
     * @return List of found requests
     */
    public Optional<List<RequestDTO>> searchAllRequests() {

        List<VolunteerRequest> foundRequests = requestRepository.findAll();

        if (!foundRequests.isEmpty()) {
            List<RequestDTO> requestDTOs = foundRequests.stream()
                    .map(request -> requestMapper.mapRequestToDTO(request))
                    .collect(Collectors.toList());
            return Optional.of(requestDTOs);
        }

        return Optional.empty();
    }

    /**
     * Searches for specific request based on id parameter
     *
     * @param id Id of searched request
     * @return Optional containing result of search
     */
    public Optional<VolunteerRequest> findRequest(Long id) {

        return requestRepository.findById(id);
    }

    public Optional<VolunteerDTO> getRequestSender(VolunteerRequest request) {

        if (this.isVolunteerSenderOrReceiver(request, volunteerService.getLoggedVolunteer()) || authenticationService.checkIfAdmin(volunteerService.getLoggedVolunteer())) {

            return Optional.of(volunteerMapper.mapVolunteerToDTO(request.getRequestSender()));
        }

        return Optional.empty();
    }

    public Optional<VolunteerDTO> getRequestReceiver(VolunteerRequest request) {

        if (this.isVolunteerSenderOrReceiver(request, volunteerService.getLoggedVolunteer()) || authenticationService.checkIfAdmin(volunteerService.getLoggedVolunteer())) {

            return Optional.of(volunteerMapper.mapVolunteerToDTO(request.getRequestSender()));
        }

        return Optional.empty();
    }

    /**
     * Deletes requests
     *
     * @param request Deleted request
     */
    public Optional<RequestDTO> deleteRequest(VolunteerRequest request) {

        if (authenticationService.checkIfAdmin(volunteerService.getLoggedVolunteer())) {

            requestRepository.delete(request);

            RequestDTO requestDTO = requestMapper.mapRequestToDTO(request);

            return Optional.of(requestDTO);
        }
        return Optional.empty();
    }

    /**
     * Checks whether volunteer is sending a request
     *
     * @param request   Inspected request
     * @param volunteer Volunteer that is checked to be a sender of request
     * @return Boolean value containing result of check
     */
    public boolean isVolunteerSender(VolunteerRequest request, Volunteer volunteer) {

        return request.getRequestSender().getId().equals(volunteer.getId());
    }

    /**
     * Checks whether volunteer is receiving a request
     *
     * @param request   Inspected request
     * @param volunteer Volunteer that is checked to be a receiver of request
     * @return Boolean value containing result of check
     */
    public boolean isVolunteerReceiver(VolunteerRequest request, Volunteer volunteer) {

        return request.getRequestReceiver().getId().equals(volunteer.getId());
    }

    /**
     * Checks whether volunteer is receiving OR sending a request
     *
     * @param request   Inspected request
     * @param volunteer Volunteer that is checked to be a receiver or sender of request
     * @return Boolean value containing result of check
     */
    public boolean isVolunteerSenderOrReceiver(VolunteerRequest request, Volunteer volunteer) {

        return isVolunteerReceiver(request, volunteer) || isVolunteerSender(request, volunteer);
    }

    /**
     * Checks whether request has a pending status
     *
     * @param request Inspected request
     * @return Boolean value containing result of check
     */
    public boolean hasPendingStatus(VolunteerRequest request) {

        return request.getStatus() == RequestStatus.PENDING;
    }

    /**
     * Creates request
     *
     * @param foundProject Project that is regarded in request
     * @return Created request
     */
    public RequestDTO createRequest(Project foundProject) {

        VolunteerRequest newRequest = VolunteerRequest.builder()
                .requestReceiver(foundProject.getOwnerVolunteer())
                .requestSender(this.volunteerService.getLoggedVolunteer())
                .requestedProject(foundProject)
                .status(RequestStatus.PENDING)
                .build();

        requestRepository.save(newRequest);

        return requestMapper.mapRequestToDTO(newRequest);
    }

    public Optional<ProjectDTO> getRequestedProject(VolunteerRequest request) {

        return Optional.of(projectMapper.mapProjectToDTO(request.getRequestedProject()));
    }

    /**
     * Declines request
     *
     * @param request Request that will be declined
     * @return Optional value that contains result of operation
     */
    public Optional<RequestDTO> declineRequest(VolunteerRequest request) {

        if ((this.isVolunteerReceiver(request, volunteerService.getLoggedVolunteer()) || authenticationService.checkIfAdmin(volunteerService.getLoggedVolunteer())) && volunteerService.getLoggedVolunteer().getOwnedProjects().contains(request.getRequestedProject()) && this.hasPendingStatus(request)) {

            request.setStatus(RequestStatus.DECLINED);
            requestRepository.save(request);

            RequestDTO requestDTO = requestMapper.mapRequestToDTO(request);

            return Optional.of(requestDTO);
        } else {

            return Optional.empty();
        }
    }

    /**
     * Accepts request
     *
     * @param request   Request that will be accepted
     * @return Optional value that contains result of operation
     */
    public Optional<RequestDTO> acceptRequest(VolunteerRequest request) {

        if ((this.isVolunteerReceiver(request, volunteerService.getLoggedVolunteer()) || authenticationService.checkIfAdmin(volunteerService.getLoggedVolunteer())) && volunteerService.getLoggedVolunteer().getOwnedProjects().contains(request.getRequestedProject()) && this.hasPendingStatus(request)) {

            request.getRequestedProject().addVolunteerToProject(request.getRequestSender());
            request.setStatus(RequestStatus.ACCEPTED);

            projectRepository.save(request.getRequestedProject());
            requestRepository.save(request);

            RequestDTO requestDTO = requestMapper.mapRequestToDTO(request);

            return Optional.of(requestDTO);
        } else {

            return Optional.empty();
        }
    }
}
