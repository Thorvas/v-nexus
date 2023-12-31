package com.example.demo.Request;

import com.example.demo.Error.CollectionEmptyException;
import com.example.demo.Error.InsufficientPermissionsException;
import com.example.demo.Error.RequestNotFoundException;
import com.example.demo.Error.WrongStatusException;
import com.example.demo.Project.Project;
import com.example.demo.Project.ProjectDTO;
import com.example.demo.Volunteer.Volunteer;
import com.example.demo.Volunteer.VolunteerDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service responsible for request operations
 *
 * @author Thorvas
 */
@Service
public class RequestService {

    @Autowired
    private RequestRepository requestRepository;

    @Autowired
    RequestServiceFacade requestServiceFacade;


    /**
     * Searches for requests
     *
     * @return List of found requests
     */
    public List<RequestDTO> searchAllRequests() {

        List<VolunteerRequest> foundRequests = requestRepository.findAll();

        if (!foundRequests.isEmpty()) {
            return foundRequests.stream()
                    .map(request -> requestServiceFacade.mapRequestToDTO(request))
                    .collect(Collectors.toList());
        }

        throw new CollectionEmptyException("There are no requests yet");
    }

    /**
     * Searches for specific request based on id parameter
     *
     * @param id Id of searched request
     * @return Result of search
     */
    public VolunteerRequest findRequest(Long id) {

        if (requestRepository.findById(id).isPresent()) {
            return requestRepository.findById(id).get();
        }

        throw new RequestNotFoundException("Request could not be found");
    }

    /**
     * Searches for sender of request
     *
     * @param requestId Id value of request
     * @return Volunteer that is sender of request
     */
    public VolunteerDTO getRequestSender(Long requestId) {

        VolunteerRequest request = this.findRequest(requestId);

        if (this.isVolunteerSenderOrReceiver(request, requestServiceFacade.getLoggedVolunteer()) || requestServiceFacade.checkIfAdmin(requestServiceFacade.getLoggedVolunteer())) {

            return requestServiceFacade.mapVolunteerToDTO(request.getRequestSender());
        }

        throw new InsufficientPermissionsException("You are not permitted to view who is the sender of request.");
    }

    /**
     * Searches for receiver of request
     *
     * @param requestId Id value of request
     * @return Volunteer that is receiver of request
     */
    public VolunteerDTO getRequestReceiver(Long requestId) {

        VolunteerRequest request = this.findRequest(requestId);

        if (this.isVolunteerSenderOrReceiver(request, requestServiceFacade.getLoggedVolunteer()) || requestServiceFacade.checkIfAdmin(requestServiceFacade.getLoggedVolunteer())) {

            return requestServiceFacade.mapVolunteerToDTO(request.getRequestSender());
        }

        throw new InsufficientPermissionsException("You are not permitted to view who is the receiver of request.");
    }

    /**
     * Deletes requests
     *
     * @param requestId Id value of request
     * @return Deleted request
     */
    public RequestDTO deleteRequest(Long requestId) {

        VolunteerRequest request = this.findRequest(requestId);

        if (requestServiceFacade.checkIfAdmin(requestServiceFacade.getLoggedVolunteer())) {

            requestRepository.delete(request);

            return requestServiceFacade.mapRequestToDTO(request);
        }

        throw new InsufficientPermissionsException("You cannot delete request because you are not an administrator");
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
     * @param projectId Id value of requested project
     * @return Created request
     */
    public RequestDTO createRequest(Long projectId) {

        Project foundProject = requestServiceFacade.findProject(projectId);
        Volunteer volunteer = requestServiceFacade.getLoggedVolunteer();

        if (requestServiceFacade.isProjectOpen(foundProject)) {
            if (!foundProject.getProjectVolunteers().contains(volunteer)) {

                VolunteerRequest newRequest = VolunteerRequest.builder()
                        .requestReceiver(foundProject.getOwnerVolunteer())
                        .requestSender(this.requestServiceFacade.getLoggedVolunteer())
                        .requestedProject(foundProject)
                        .status(RequestStatus.PENDING)
                        .build();

                requestRepository.save(newRequest);

                return requestServiceFacade.mapRequestToDTO(newRequest);
            }

            throw new InsufficientPermissionsException("You already participate in this project");
        }
        throw new WrongStatusException("Project doesn't accept any new participants");

    }

    /**
     * Searches for request in database using utility method
     *
     * @param requestId Id value of request
     * @return Result of search
     */
    public RequestDTO searchRequest(Long requestId) {

        VolunteerRequest request = this.findRequest(requestId);

        return requestServiceFacade.mapRequestToDTO(request);

    }

    /**
     * Searches for project of request based on id parameter
     *
     * @param requestId Id value of request
     * @return Project that is associated with request
     */
    public ProjectDTO getRequestedProject(Long requestId) {

        VolunteerRequest request = this.findRequest(requestId);

        return requestServiceFacade.mapProjectToDTO(request.getRequestedProject());
    }

    /**
     * Declines request
     *
     * @param requestId Id value of request
     * @return Request that is declined
     */
    public RequestDTO declineRequest(Long requestId) {

        VolunteerRequest request = this.findRequest(requestId);

        if (this.isVolunteerReceiver(request, requestServiceFacade.getLoggedVolunteer()) || requestServiceFacade.checkIfAdmin(requestServiceFacade.getLoggedVolunteer())) {
            if (requestServiceFacade.getLoggedVolunteer().getOwnedProjects().contains(request.getRequestedProject())) {
                if (this.hasPendingStatus(request)) {
                    request.setStatus(RequestStatus.DECLINED);
                    requestRepository.save(request);

                    return requestServiceFacade.mapRequestToDTO(request);
                }

                throw new WrongStatusException("Request doesn't have pending status");
            }

            throw new RequestNotFoundException("Requested project is not owned by this volunteer");
        }

        throw new InsufficientPermissionsException("You are not permitted to decline this request");
    }

    /**
     * Accepts request
     *
     * @param requestId Id value of request
     * @return Request that is accepted
     */
    public RequestDTO acceptRequest(Long requestId) {

        VolunteerRequest request = this.findRequest(requestId);

        if ((this.isVolunteerReceiver(request, requestServiceFacade.getLoggedVolunteer()) || requestServiceFacade.checkIfAdmin(requestServiceFacade.getLoggedVolunteer()))) {
            if (requestServiceFacade.getLoggedVolunteer().getOwnedProjects().contains(request.getRequestedProject())) {
                if (this.hasPendingStatus(request)) {
                    if (!requestServiceFacade.isProjectFull(request.getRequestedProject())) {

                        request.getRequestedProject().addVolunteerToProject(request.getRequestSender());
                        request.setStatus(RequestStatus.ACCEPTED);

                        requestServiceFacade.saveProject(request.getRequestedProject());
                        requestRepository.save(request);

                        return requestServiceFacade.mapRequestToDTO(request);
                    }

                    throw new WrongStatusException("Project is already full");
                }

                throw new WrongStatusException("Request doesn't have pending status");
            }

            throw new RequestNotFoundException("Requested project is not owned by this volunteer");
        }

        throw new InsufficientPermissionsException("You are not permitted to accept this request");
    }
}
